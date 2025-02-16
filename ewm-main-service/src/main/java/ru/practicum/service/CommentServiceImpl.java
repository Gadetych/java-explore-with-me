package ru.practicum.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CommentResponseDto;
import ru.practicum.enums.StateOfPublication;
import ru.practicum.exception.conflict.PublicationEventException;
import ru.practicum.exception.forbidden.AccessDeniedToCommentException;
import ru.practicum.exception.not_found.NotFoundException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.QComment;
import ru.practicum.model.User;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UsersRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UsersRepository usersRepository;
    private final EventRepository eventRepository;

    //    Private
    @Transactional
    @Override
    public CommentResponseDto create(Long userId, Long eventId, CommentDto requestBody) {
        log.debug("==> Create comment for event: user: {} event: {}, request body: {}", userId, eventId, requestBody);
        User author = usersRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
        if (event.getState() != StateOfPublication.PUBLISHED) {
            throw new PublicationEventException("Event not published");
        }
        Comment model = CommentMapper.dtoToMapper(author, event, requestBody);
        model = commentRepository.save(model);
        CommentResponseDto result = CommentMapper.modelToDto(model);
        log.debug("<== Created comment: {}", result);
        return result;
    }

    @Transactional
    @Override
    public CommentResponseDto change(Long userId, Long comId, CommentDto requestBody) {
        log.debug("==> Change comment: user: {} comment id: {}, request body: {} ", userId, comId, requestBody);
        Comment model = commentRepository.findById(comId).orElseThrow(() -> new NotFoundException("Comment not found"));
        if (model.getAuthor().getId() != userId) {
            throw new AccessDeniedToCommentException("You do not have permission to edit this comment.");
        }
        model.setText(requestBody.getText());
        model = commentRepository.save(model);
        CommentResponseDto result = CommentMapper.modelToDto(model);
        log.debug("<== Changed comment: {}", result);
        return result;
    }

    @Transactional
    @Override
    public void delete(Long userId, Long comId) {
        log.debug("==> Delete comment: user: {} comment id: {}", userId, comId);
        Comment model = commentRepository.findById(comId).orElseThrow(() -> new NotFoundException("Comment not found"));
        if (model.getAuthor().getId() != userId) {
            throw new AccessDeniedToCommentException("You do not have permission to edit this comment.");
        }
        commentRepository.deleteById(comId);
        log.debug("<== Deleted comment)");
    }

    //    Admin
    @Override
    public void delete(Long comId) {
        log.debug("==> Delete comment id: {}", comId);
        commentRepository.deleteById(comId);
        log.debug("<== Deleted comment)");
    }

    //    Public
    @Override
    public List<CommentResponseDto> findAllByEventId(Long eventId, Integer from, Integer size) {
        log.debug("==> Find all comments by event id: {}", eventId);
        BooleanExpression predicate = QComment.comment.event.id.eq(eventId);
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageRequest = PageRequest.of(from / size, size, sort);
        List<Comment> models = commentRepository.findAll(predicate, pageRequest).stream().toList();
        if (models.isEmpty()) {
            return List.of();
        }
        List<CommentResponseDto> result = models.stream()
                .map(CommentMapper::modelToDto)
                .toList();
        log.debug("<== Found all comments: {}", result);
        return result;
    }
}
