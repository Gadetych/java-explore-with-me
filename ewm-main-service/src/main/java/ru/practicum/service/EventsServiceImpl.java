package ru.practicum.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.AdminParamEvent;
import ru.practicum.dto.event.BaseUpdateEventRequest;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.PublicParamEvent;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.request.ConfirmedRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.enums.StateActionUser;
import ru.practicum.enums.StateOfPublication;
import ru.practicum.enums.StatusParticipationRequest;
import ru.practicum.ewm.stats.client.StatClient;
import ru.practicum.ewm.stats.common.dto.ViewStatsResponseDto;
import ru.practicum.exception.conflict.PublicationEventException;
import ru.practicum.exception.conflict.RequestModificationException;
import ru.practicum.exception.not_found.NotFoundException;
import ru.practicum.exception.validation.EventModificationException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.LocationMapper;
import ru.practicum.mapper.RequestsMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.QEvent;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.repository.CategoriesRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UsersRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EventsServiceImpl implements EventsService {
    private final EventRepository eventRepository;
    private final StatClient statClient;
    private final CategoriesRepository categoriesRepository;
    private final UsersRepository usersRepository;
    private final RequestRepository requestRepository;

    //Private
    @Override
    public List<EventShortDto> findAll(long userId, int from, int size) {
        log.debug("==> Find all events for userId {}, from {}, size {} ", userId, from, size);
        BooleanExpression predicate = QEvent.event.initiator.id.eq(userId);
        List<Event> events = getEvents(predicate, from, size);
        List<EventShortDto> result = getEventsShortDto(events);
        log.debug("<== Find all events short dto {} ", result);
        return result;
    }

    private List<Event> getEvents(BooleanExpression predicate, int from, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageRequest = PageRequest.of(from / size, size, sort);
        return eventRepository.findAll(predicate, pageRequest).toList();
    }

    private List<EventShortDto> getEventsShortDto(List<Event> events) {
        if (events.isEmpty()) {
            return Collections.emptyList();
        }
        LocalDateTime earliestDate = LocalDateTime.MAX;
        LocalDateTime distantDate = LocalDateTime.MIN;
        Map<Long, String> eventUriMap = new HashMap<>();
        for (Event event : events) {
            eventUriMap.put(event.getId(), "/events/" + event.getId());
            if (earliestDate.isAfter(event.getEventDate())) {
                earliestDate = event.getEventDate();
            }
            if (distantDate.isBefore(event.getEventDate())) {
                distantDate = event.getEventDate();
            }
        }
        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(new ArrayList<>(eventUriMap.keySet()));
        Map<String, Long> viewsMap = getViewsMap(new ArrayList<>(eventUriMap.values()), earliestDate, distantDate);
        List<EventShortDto> result = new ArrayList<>();
        for (Event event : events) {
            long confirmedRequest = confirmedRequestsMap.getOrDefault(event.getId(), 0L);
            long views = viewsMap.getOrDefault("/events/" + event.getId(), 0L);
            result.add(EventMapper.modelToShortDto(event, confirmedRequest, views));
        }
        return result;
    }

    private Map<Long, Long> getConfirmedRequestsMap(List<Long> eventIds) {
        List<ConfirmedRequest> listConfirmedRequests = requestRepository.getConfirmedRequestsByStatus(eventIds, StatusParticipationRequest.CONFIRMED);
        Map<Long, Long> confirmedRequestsMap = new HashMap<>();
        for (ConfirmedRequest request : listConfirmedRequests) {
            confirmedRequestsMap.put(request.getEventId(), request.getConfirmedCountRequests());
        }
        return confirmedRequestsMap;
    }

    private Map<String, Long> getViewsMap(List<String> uris, LocalDateTime earliestDate, LocalDateTime distantDate) {
        boolean unique = true;
        List<ViewStatsResponseDto> viewStats = statClient.getViewStats(earliestDate, distantDate, uris, unique);
        Map<String, Long> viewsMap = new HashMap<>();
        for (ViewStatsResponseDto stat : viewStats) {
            viewsMap.put(stat.getUri(), stat.getHits());
        }
        return viewsMap;
    }

    @Transactional
    @Override
    public EventFullDto create(long userId, NewEventDto requestBody) {
        log.debug("==> Create new event {} for userId {}", requestBody, userId);
        Category category = categoriesRepository.findById(requestBody.getCategory()).orElseThrow(() -> new NotFoundException("Category not found by id: " + requestBody.getCategory()));
        User user = usersRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        Event model = EventMapper.newEventDtoToModel(requestBody, category, user);
        long eventId = eventRepository.save(model).getId();
        model = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found by id: " + eventId));
        long confirmedRequests = 0L;
        long views = 0;
        log.debug("<== Create new event {} for userId {}", model, userId + "");
        return EventMapper.modelToFullDto(model, confirmedRequests, views);
    }

    @Override
    public EventFullDto findById(long userId, long eventId) {
        log.debug("==> User finds his event: eventId {} for userId {}", eventId, userId);
        Event model = eventRepository.findByInitiatorIdAndId(userId, eventId).orElseThrow(() -> new NotFoundException("Event not found by id: " + eventId));
        long confirmedRequests = getConfirmedRequests(eventId);
        long views = getViews(eventId, model);
        EventFullDto result = EventMapper.modelToFullDto(model, confirmedRequests, views);
        log.debug("<== User finds his event: result {}", result);
        return result;
    }

    @Transactional
    @Override
    public EventFullDto update(long userId, long eventId, UpdateEventUserRequest requestBody) {
        log.debug("==> User update of the event: request body {} for userId {} adn eventId {}", requestBody, userId, eventId);
        Event model = changeEventByUser(eventId, requestBody);
        EventFullDto result = getDataForMapping(model);
        log.debug("<=== User update of the event: result {}", result);
        return result;
    }

    private EventFullDto getDataForMapping(Event model) {
        long eventId = model.getId();
        long confirmedRequests = getConfirmedRequests(eventId);
        long views = getViews(eventId, model);
        return EventMapper.modelToFullDto(model, confirmedRequests, views);
    }

    private Event changeEventByUser(long eventId, UpdateEventUserRequest requestBody) {
        Event model = makeChangesEventParams(eventId, requestBody);
        if (requestBody.getStateAction() != null) {
            model.setState(requestBody.getStateAction().equals(StateActionUser.CANCEL_REVIEW) ? StateOfPublication.CANCELED : StateOfPublication.PENDING);
        }
        model = eventRepository.save(model);
        return model;
    }

    private Event makeChangesEventParams(long eventId, BaseUpdateEventRequest requestBody) {
        Event model = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found by id: " + eventId));
        if (model.getState().equals(StateOfPublication.PUBLISHED)) {
            throw new PublicationEventException("Only pending or canceled events can be changed");
        }
        if (requestBody.getCategory() != null) {
            Category category = categoriesRepository.findById(requestBody.getCategory()).orElseThrow(() -> new NotFoundException("Category not found by id=" + requestBody.getCategory()));
            model.setCategory(category);
        }
        if (requestBody.getAnnotation() != null) {
            model.setAnnotation(requestBody.getAnnotation());
        }
        if (requestBody.getDescription() != null) {
            model.setDescription(requestBody.getDescription());
        }
        if (requestBody.getEventDate() != null) {
            if (requestBody.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
                model.setEventDate(requestBody.getEventDate());
            } else {
                throw new EventModificationException("The date and time on which the event is scheduled cannot be earlier than two hours from the current moment.");
            }
        }
        if (requestBody.getLocation() != null) {
            model.setLocation(LocationMapper.dtoToModel(requestBody.getLocation()));
        }
        if (requestBody.getPaid() != null) {
            model.setPaid(requestBody.getPaid());
        }
        if (requestBody.getParticipantLimit() != null) {
            model.setParticipantLimit(requestBody.getParticipantLimit());
        }
        if (requestBody.getRequestModeration() != null) {
            model.setRequestModeration(requestBody.getRequestModeration());
        }
        if (requestBody.getTitle() != null) {
            model.setTitle(requestBody.getTitle());
        }
        return model;
    }

    private long getConfirmedRequests(long eventId) {
        List<ConfirmedRequest> listConfirmedRequests = requestRepository.getConfirmedRequestsByStatus(List.of(eventId), StatusParticipationRequest.CONFIRMED);
        return listConfirmedRequests.isEmpty() ? 0 : listConfirmedRequests.getFirst().getConfirmedCountRequests();
    }

    private long getViews(long eventId, Event model) {
        String uri = "/events/" + eventId;
        boolean unique = true;
        List<Long> listViews = statClient.getViewStats(model.getCreatedOn(), model.getEventDate(), List.of(uri), unique).stream()
                .map(ViewStatsResponseDto::getHits)
                .toList();
        return listViews.isEmpty() ? 0 : listViews.getFirst();
    }

    @Override
    public List<ParticipationRequestDto> findRequests(long userId, long eventId) {
        log.debug("==> Find requests for event {}, userId {}", eventId, userId);
        List<Request> requests = requestRepository.findAllByEventId(eventId);
        log.debug("<== Find requests {}", requests);
        return requests.stream()
                .map(RequestsMapper::modelToDto)
                .toList();
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateStatusRequest(long userId, long eventId, EventRequestStatusUpdateRequest requestBody) {
        log.debug("==> Update status request {} for eventId {}, userId {}", requestBody, eventId, userId);
        Event eventModel = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found by id: " + eventId));
        List<ConfirmedRequest> listConfirmedRequests = requestRepository.getConfirmedRequestsByStatus(List.of(eventId), StatusParticipationRequest.CONFIRMED);
        long numberConfirmedRequests = listConfirmedRequests.isEmpty() ? 0 : listConfirmedRequests.getFirst().getConfirmedCountRequests();
        if (eventModel.getParticipantLimit() <= numberConfirmedRequests) {
            throw new RequestModificationException("The participant limit has been reached");
        }
        if (eventModel.getParticipantLimit() != 0 && eventModel.isRequestModeration()) {
            changeStatusRequests(eventId, requestBody, eventModel, numberConfirmedRequests);
        }
        Map<StatusParticipationRequest, List<ParticipationRequestDto>> map = splitRequestsByStatus(eventId);
        log.debug("<== Update status confirmed {}, rejected {}", map.get(StatusParticipationRequest.CONFIRMED), map.get(StatusParticipationRequest.REJECTED));
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(map.get(StatusParticipationRequest.CONFIRMED))
                .rejectedRequests(map.get(StatusParticipationRequest.REJECTED))
                .build();
    }

    private void changeStatusRequests(long eventId, EventRequestStatusUpdateRequest requestBody, Event eventModel, long numberConfirmedRequests) {
        StatusParticipationRequest newStatus = requestBody.getStatus();
        List<Long> requestIds = requestBody.getRequestIds();
        List<Request> requestsForUpdateStatus = requestRepository.findAllByIdIn(requestIds);
        for (Request request : requestsForUpdateStatus) {
            if (request.getStatus().equals(StatusParticipationRequest.PENDING)) {
                request.setStatus(newStatus);
                ++numberConfirmedRequests;
                if (eventModel.getParticipantLimit() <= numberConfirmedRequests) {
                    rejectUnconfirmedRequests(eventId);
                    break;
                }
            } else throw new RequestModificationException("Status can be changed only for pending requests");
            requestRepository.saveAll(requestsForUpdateStatus);
        }
    }

    private void rejectUnconfirmedRequests(long eventId) {
        List<Request> requests = requestRepository.findAllByStatusInAndEventIdOrderByStatus(List.of(StatusParticipationRequest.PENDING), eventId);
        for (Request request : requests) {
            request.setStatus(StatusParticipationRequest.REJECTED);
        }
        requestRepository.saveAll(requests);
    }

    private Map<StatusParticipationRequest, List<ParticipationRequestDto>> splitRequestsByStatus(long eventId) {
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
        List<Request> requests = requestRepository.findAllByStatusInAndEventIdOrderByStatus(List.of(StatusParticipationRequest.CONFIRMED, StatusParticipationRequest.REJECTED), eventId);
        for (Request request : requests) {
            ParticipationRequestDto dto = RequestsMapper.modelToDto(request);
            if (dto.getStatus().equals(StatusParticipationRequest.CONFIRMED)) {
                confirmedRequests.add(dto);
            } else rejectedRequests.add(dto);
        }
        return Map.of(StatusParticipationRequest.CONFIRMED, confirmedRequests, StatusParticipationRequest.REJECTED, rejectedRequests);
    }

    //    Admin
    @Override
    public List<EventFullDto> findAll(AdminParamEvent paramSearch) {
        log.debug("==> Find all events paramSearch {}", paramSearch);
        BooleanExpression predicate = selectPredicate(paramSearch);
        List<Event> events = getEvents(predicate, paramSearch.getFrom(), paramSearch.getSize());
        List<EventFullDto> result = getEventsFullDto(events);
        log.debug("<== Find all events result {}", result);
        return result;
    }

    private BooleanExpression selectPredicate(AdminParamEvent paramSearch) {
        BooleanExpression predicate = QEvent.event.isNotNull();
        if (paramSearch.getUsers() != null && !paramSearch.getUsers().isEmpty()) {
            predicate = predicate.and(QEvent.event.initiator.id.in(paramSearch.getUsers()));
        }
        if (paramSearch.getStates() != null && !paramSearch.getStates().isEmpty()) {
            predicate = predicate.and(QEvent.event.state.in(paramSearch.getStates()));
        }
        if (paramSearch.getCategories() != null && !paramSearch.getCategories().isEmpty()) {
            predicate = predicate.and(QEvent.event.category.id.in(paramSearch.getCategories()));
        }
        if (paramSearch.getRangeStart() != null) {
            predicate = predicate.and(QEvent.event.eventDate.goe(paramSearch.getRangeStart()));
        }
        if (paramSearch.getRangeEnd() != null) {
            predicate = predicate.and(QEvent.event.eventDate.loe(paramSearch.getRangeEnd()));
        }
        return predicate;
    }

    private List<EventFullDto> getEventsFullDto(List<Event> events) {
        if (events.isEmpty()) {
            return Collections.emptyList();
        }
        LocalDateTime earliestDate = LocalDateTime.MAX;
        LocalDateTime distantDate = LocalDateTime.MIN;
        Map<Long, String> eventUriMap = new HashMap<>();
        for (Event event : events) {
            eventUriMap.put(event.getId(), "/events/" + event.getId());
            if (earliestDate.isAfter(event.getEventDate())) {
                earliestDate = event.getEventDate();
            }
            if (distantDate.isBefore(event.getEventDate())) {
                distantDate = event.getEventDate();
            }
        }
        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(new ArrayList<>(eventUriMap.keySet()));
        Map<String, Long> viewsMap = getViewsMap(new ArrayList<>(eventUriMap.values()), earliestDate, distantDate);
        List<EventFullDto> result = new ArrayList<>();
        for (Event event : events) {
            long confirmedRequest = confirmedRequestsMap.getOrDefault(event.getId(), 0L);
            long views = viewsMap.getOrDefault("/events/" + event.getId(), 0L);
            result.add(EventMapper.modelToFullDto(event, confirmedRequest, views));
        }
        return result;
    }

    @Transactional
    @Override
    public EventFullDto update(Long eventId, UpdateEventAdminRequest requestBody) {
        log.debug("==> Admin update of the event: request body {}, eventId {}", requestBody, eventId);
        Event model = changeEventByAdmin(eventId, requestBody);
        EventFullDto result = getDataForMapping(model);
        log.debug("<=== Admin update of the event: result {}", result);
        return result;
    }

    private Event changeEventByAdmin(long eventId, UpdateEventAdminRequest requestBody) {
        Event model = makeChangesEventParams(eventId, requestBody);
        if (requestBody.getStateAction() != null && model.getState().equals(StateOfPublication.PENDING)) {
            switch (requestBody.getStateAction()) {
                case REJECT_EVENT -> model.setState(StateOfPublication.CANCELED);
                case PUBLISH_EVENT -> {
                    model.setState(StateOfPublication.PUBLISHED);
                    model.setPublishedOn(LocalDateTime.now());
                }
            }
        } else if (!model.getState().equals(StateOfPublication.PENDING)) {
            throw new PublicationEventException("Can't change state of event " + model.getId() + " because event state is " + model.getState());
        }
        model = eventRepository.save(model);
        return model;
    }

    //    Public
    @EntityGraph(attributePaths = {"category", "initiator"})
    @Override
    public List<EventShortDto> findAll(PublicParamEvent paramSearch) {
        log.debug("==> Find all events, paramSearch {}", paramSearch);
        BooleanExpression predicate = selectPredicate(paramSearch);
        List<Event> events = getEvents(predicate, paramSearch.getFrom(), paramSearch.getSize());
        List<EventShortDto> result = getEventsShortDto(events);

        Comparator<EventShortDto> comparator = switch (paramSearch.getSort()) {
            case EVENT_DATE -> comparator = Comparator.comparing(EventShortDto::getEventDate);
            case VIEWS -> comparator = Comparator.comparing(EventShortDto::getViews);
            case null -> comparator = Comparator.comparing(EventShortDto::getId);
        };

        Stream<EventShortDto> stream = result.stream().sorted(comparator);
        if (paramSearch.getOnlyAvailable()) {
            stream = stream.filter((dto) -> dto.getConfirmedRequests() < dto.getParticipantLimit());
        }

        result = stream.toList();
        log.debug("<=== Find all events {}", result);
        return result;
    }

    private BooleanExpression selectPredicate(PublicParamEvent paramSearch) {
        QEvent qEvent = QEvent.event;
        BooleanExpression predicate = qEvent.state.eq(StateOfPublication.PUBLISHED);
        if (paramSearch.getText() != null) {
            predicate = predicate.and(qEvent.annotation.likeIgnoreCase(paramSearch.getText())).or(qEvent.description.likeIgnoreCase(paramSearch.getText()));
        }
        if (paramSearch.getCategories() != null && !paramSearch.getCategories().isEmpty()) {
            predicate = predicate.and(qEvent.category.id.in(paramSearch.getCategories()));
        }
        if (paramSearch.getPaid() != null) {
            predicate = predicate.and(qEvent.paid.eq(paramSearch.getPaid()));
        }
        if (paramSearch.getRangeStart() != null && paramSearch.getRangeEnd() != null) {
            predicate = predicate.and(qEvent.eventDate.between(paramSearch.getRangeStart(), paramSearch.getRangeEnd()));
        } else {
            predicate = predicate.and(qEvent.eventDate.after(LocalDateTime.now()));
        }
        return predicate;
    }

    @Override
    public EventFullDto findById(Long eventId) {
        log.debug("==> Find the event: eventId {}", eventId);
        Event model = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found by id: " + eventId));
        if (!model.getState().equals(StateOfPublication.PUBLISHED)) {
            throw new NotFoundException("Event is not published");
        }
        long confirmedRequests = getConfirmedRequests(eventId);
        long views = getViews(eventId, model);

        EventFullDto result = EventMapper.modelToFullDto(model, confirmedRequests, views);
        log.debug("<== Find the event: result {}", result);
        return result;
    }

    @Override
    public List<EventFullDto> findAllById(List<Long> eventIds) {
        log.debug("==> Find all events by id {}", eventIds);
        List<Event> events = eventRepository.findAllById(eventIds);
        List<EventFullDto> result = getEventsFullDto(events);
        log.debug("<== Find all events: {}", result);
        return result;
    }

}
