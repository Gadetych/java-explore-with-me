package ru.practicum.service;

import ru.practicum.dto.request.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> findAll(Long userId);

    ParticipationRequestDto create(long userId, long eventId);

    ParticipationRequestDto update(long userId, long requestId);
}
