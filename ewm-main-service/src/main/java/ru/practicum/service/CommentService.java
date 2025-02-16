package ru.practicum.service;

import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CommentResponseDto;

import java.util.List;

public interface CommentService {
    CommentResponseDto create(Long userId, Long eventId, CommentDto requestBody);

    CommentResponseDto change(Long userId, Long comId, CommentDto requestBody);

    void delete(Long userId, Long comId);

    void delete(Long comId);

    List<CommentResponseDto> findAllByEventId(Long eventId, Integer from, Integer size);
}
