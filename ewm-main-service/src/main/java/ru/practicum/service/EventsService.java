package ru.practicum.service;

import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;

import java.util.List;

public interface EventsService {
    List<EventShortDto> findAll(long userId, int from, int size);

    EventFullDto create(long userId, NewEventDto requestBody);

    EventFullDto findById(long userId, long eventId);

    EventFullDto update(long userId, long eventId, UpdateEventUserRequest requestBody);

    List<ParticipationRequestDto> findRequests(long userId, long eventId);

    EventRequestStatusUpdateResult updateStatusRequest(long userId, long eventId, EventRequestStatusUpdateRequest requestBody);
}
