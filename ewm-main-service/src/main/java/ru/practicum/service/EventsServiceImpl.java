package ru.practicum.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.AdminParamEvent;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.enums.StateActionAdmin;
import ru.practicum.enums.StateActionUser;
import ru.practicum.enums.StateOfPublication;
import ru.practicum.enums.StatusParticipationRequest;
import ru.practicum.ewm.stats.client.StatClient;
import ru.practicum.ewm.stats.common.dto.ViewStatsResponseDto;
import ru.practicum.exception.EventModificationException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.RequestModificationException;
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
import java.util.List;
import java.util.Map;

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
        List<Event> events = eventRepository.findAllLimitOrderByCreated(userId, from, size);
        if (events.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> eventIds = new ArrayList<>();
        List<String> uris = new ArrayList<>();
        collectEventIdsAndUris(events, eventIds, uris);
        List<Integer> listConfirmedRequests = requestRepository.getIdsRequestsByStatus(eventIds, StatusParticipationRequest.CONFIRMED);
        boolean unique = false;
        List<ViewStatsResponseDto> viewStats = statClient.getViewStats(events.get(0).getCreatedOn(), events.get(events.size() - 1).getEventDate(), uris, unique);
        List<EventShortDto> result = new ArrayList<>();
        mappingListEventToListShortDto(events, result, listConfirmedRequests, viewStats);
        log.debug("<== Find all events short dto {} ", result);
        return result;
    }

    private void collectEventIdsAndUris(List<Event> events, List<Long> eventIds, List<String> uris) {
        for (Event event : events) {
            eventIds.add(event.getId());
            uris.add("/events/" + event.getId());
        }
    }

    private void mappingListEventToListShortDto(List<Event> events, List<EventShortDto> result, List<Integer> listConfirmedRequests, List<ViewStatsResponseDto> viewStats) {
        for (int i = 0; i < events.size(); i++) {
            int confirmedRequest = listConfirmedRequests.isEmpty() ? 0 : listConfirmedRequests.get(i);
            long views = viewStats.isEmpty() ? 0 : viewStats.get(i).getHits();
            result.add(EventMapper.modelToShortDto(events.get(i), confirmedRequest, views));
        }
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
//        Эти параметры должны быть 0 при создании события
        int confirmedRequests = 0;
        long views = 0;
        log.debug("<== Create new event {} for userId {}", model, userId + "");
        return EventMapper.modelToFullDto(model, confirmedRequests, views);
    }

    @Override
    public EventFullDto findById(long userId, long eventId) {
        log.debug("==> Find event {} for userId {}", eventId, userId);
        Event model = eventRepository.findByInitiatorIdAndId(userId, eventId).orElseThrow(() -> new NotFoundException("Event not found by id: " + eventId));
        List<Integer> listConfirmedRequests = requestRepository.getIdsRequestsByStatus(List.of(eventId), StatusParticipationRequest.CONFIRMED);
        int confirmedRequests = listConfirmedRequests.isEmpty() ? 0 : listConfirmedRequests.getFirst();
        String uri = "/events/" + eventId;
        boolean unique = false;
        List<ViewStatsResponseDto> listViewStats = statClient.getViewStats(model.getCreatedOn(), model.getEventDate(), List.of(uri), unique);
        long views = listViewStats.isEmpty() ? 0 : listViewStats.get(0).getHits();
        log.debug("<== Find event {} for userId {}", model, userId + "");
        return EventMapper.modelToFullDto(model, confirmedRequests, views);
    }

    @Transactional
    @Override
    public EventFullDto update(long userId, long eventId, UpdateEventUserRequest requestBody) {
        log.debug("==> Update event {} for userId {} adn eventId {}", requestBody, userId, eventId);
        Event model = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found by id: " + eventId));
        makeChangesEventParams(model, requestBody);
        model = eventRepository.save(model);
        List<Integer> listConfirmedRequests = requestRepository.getIdsRequestsByStatus(List.of(eventId), StatusParticipationRequest.CONFIRMED);
        int confirmedRequests = listConfirmedRequests.isEmpty() ? 0 : listConfirmedRequests.getFirst();
        String uri = "/events/" + eventId;
        boolean unique = false;
        List<Long> listViews = statClient.getViewStats(model.getCreatedOn(), model.getEventDate(), List.of(uri), unique).stream()
                .map(ViewStatsResponseDto::getHits)
                .toList();
        long views = listViews.isEmpty() ? 0 : listViews.get(0);
        log.debug("<=== Update event model {} confirmed requests {}, views {}", model, confirmedRequests, views);
        return EventMapper.modelToFullDto(model, confirmedRequests, views);
    }

    private void makeChangesEventParams(Event model, UpdateEventUserRequest requestBody) {
        if (model.getState().equals(StateOfPublication.PUBLISHED)) {
            throw new EventModificationException("Only pending or canceled events can be changed");
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
        if (requestBody.getStateAction() != null) {
            model.setState(requestBody.getStateAction().equals(StateActionUser.CANCEL_REVIEW) ? StateOfPublication.CANCELED : StateOfPublication.PUBLISHED);
        }
        if (requestBody.getTitle() != null) {
            model.setTitle(requestBody.getTitle());
        }
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
        List<Integer> listConfirmedRequests = requestRepository.getIdsRequestsByStatus(List.of(eventId), StatusParticipationRequest.CONFIRMED);
        int numberConfirmedRequests = listConfirmedRequests.isEmpty() ? 0 : listConfirmedRequests.getFirst();
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

    private void changeStatusRequests(long eventId, EventRequestStatusUpdateRequest requestBody, Event eventModel, int numberConfirmedRequests) {
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
        log.debug("==> Find all events parameters {}", paramSearch);
        BooleanExpression predicate = QEvent.event.isNotNull();
        predicate = selectPredicate(predicate, paramSearch);
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pr = PageRequest.of(paramSearch.getFrom() / paramSearch.getSize(), paramSearch.getSize(), sort);
        List<Event> events = eventRepository.findAll(predicate, pr).stream().toList();
        List<Long> eventIds = new ArrayList<>();
        List<String> uris = new ArrayList<>();
        collectEventIdsAndUris(events, eventIds, uris);
        List<Integer> listConfirmedRequests = requestRepository.getIdsRequestsByStatus(eventIds, StatusParticipationRequest.CONFIRMED);
        boolean unique = false;
        List<ViewStatsResponseDto> viewStats = statClient.getViewStats(events.get(0).getCreatedOn(), events.get(events.size() - 1).getEventDate(), uris, unique);
        List<EventFullDto> result = new ArrayList<>();
        mappingListEventToListFullDto(events, result, listConfirmedRequests, viewStats);
        log.debug("<== Find all events result {}", result);
        return result;
    }

    private BooleanExpression selectPredicate(BooleanExpression predicate, AdminParamEvent paramSearch) {
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

    private void mappingListEventToListFullDto(List<Event> events, List<EventFullDto> result, List<Integer> listConfirmedRequests, List<ViewStatsResponseDto> viewStats) {
        for (int i = 0; i < events.size(); i++) {
            int confirmedRequest = listConfirmedRequests.isEmpty() ? 0 : listConfirmedRequests.get(i);
            long views = viewStats.isEmpty() ? 0 : viewStats.get(i).getHits();
            result.add(EventMapper.modelToFullDto(events.get(i), confirmedRequest, views));
        }
    }

    @Transactional
    @Override
    public EventFullDto update(Long eventId, UpdateEventAdminRequest requestBody) {
        log.debug("==> Update event {} and eventId {}", requestBody, eventId);
        Event model = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found by id: " + eventId));
        makeChangesEventParams(model, requestBody);
        model = eventRepository.save(model);
        List<Integer> listConfirmedRequests = requestRepository.getIdsRequestsByStatus(List.of(eventId), StatusParticipationRequest.CONFIRMED);
        int confirmedRequests = listConfirmedRequests.isEmpty() ? 0 : listConfirmedRequests.getFirst();
        String uri = "/events/" + eventId;
        boolean unique = false;
        List<Long> listViews = statClient.getViewStats(model.getCreatedOn(), model.getEventDate(), List.of(uri), unique).stream()
                .map(ViewStatsResponseDto::getHits)
                .toList();
        long views = listViews.isEmpty() ? 0 : listViews.get(0);
        log.debug("<=== Update event model {} confirmed requests {}, views {}", model, confirmedRequests, views);
        return EventMapper.modelToFullDto(model, confirmedRequests, views);
    }

    private void makeChangesEventParams(Event model, UpdateEventAdminRequest requestBody) {
        if (!model.getState().equals(StateOfPublication.PENDING)) {
            throw new EventModificationException("Only pending or canceled events can be changed");
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
            if (requestBody.getEventDate().isAfter(LocalDateTime.now().plusHours(1))) {
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
        if (requestBody.getStateAction() != null) {
            if (requestBody.getStateAction().equals(StateActionAdmin.REJECT_EVENT)) {
                model.setState(StateOfPublication.CANCELED);
            } else {
                model.setState(StateOfPublication.PUBLISHED);
                model.setPublishedOn(LocalDateTime.now());
            }
        }
        if (requestBody.getTitle() != null) {
            model.setTitle(requestBody.getTitle());
        }
    }

}
