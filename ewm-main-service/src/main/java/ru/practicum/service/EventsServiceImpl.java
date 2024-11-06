package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.ewm.stats.client.StatClient;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.LocationMapper;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.LocationRepository;
import ru.practicum.repository.UsersRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EventsServiceImpl implements EventsService {
    private final EventRepository eventRepository;
    private final StatClient statClient;
    private final CategoriesService categoriesService;
    private final UsersRepository usersRepository;
    private final LocationRepository locationRepository;

    //Private
    @Override
    public List<EventShortDto> findAll(long userId, int from, int size) {
        log.debug("==> Find all events for userId {}, from {}, size {} ", userId, from, size);
//       TODO: Необходимо получить статистику посещений и число принятых заявок
        List<Event> result = eventRepository.findAllLimit(userId, from, size);
//        int confirmedRequests = repository.
//        List<ViewStatsResponseDto> viewStats = statClient.getViewStats()
        log.debug("<== Find all events {} ", result);
        return result.stream()
                .map(EventMapper::modelToShortDto)
                .toList();
    }

    @Transactional
    @Override
    public EventFullDto create(long userId, NewEventDto requestBody) {
        log.debug("==> Create new event {} for userId {}", requestBody, userId);
//        TODO Проверить существование категории и пользователя
//         (имеет ли смысл проверять пользователя, если он уже авторизовался??)
        CategoryDto categoryDto = categoriesService.findById(requestBody.getCategory());
        User user = usersRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        Location location = locationRepository.save(LocationMapper.dtoToModel(requestBody.getLocation()));
//        TODO добавить получение статистики посещений
//        TODO добавить статистику по одобренным запросам
        Event model = EventMapper.newEventDtoToModel(requestBody, categoryDto, user, location);
        long eventId = eventRepository.save(model).getId();
        model = eventRepository.findById(eventId).get();
        return EventMapper.modelToFullDto(model);
    }

    @Override
    public EventFullDto findById(long userId, long eventId) {
        return null;
    }

    @Transactional
    @Override
    public EventFullDto update(long userId, long eventId, UpdateEventUserRequest requestBody) {
        return null;
    }
}
