package ru.practicum.mapper;

import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.time.LocalDateTime;

public class CommentMapper {

    public static Comment dtoToMapper(User author, Event event, NewCommentDto requestBody) {
        return Comment.builder()
                .author(author)
                .event(event)
                .text(requestBody.getText())
                .created(LocalDateTime.now())
                .build();
    }

    public static CommentDto modelToDto(Comment model) {
        return CommentDto.builder()
                .id(model.getId())
                .created(model.getCreated())
                .text(model.getText())
                .author(UserMapper.modelToUserShortDto(model.getAuthor()))
                .event(EventMapper.modelToEventTitleDto(model.getEvent()))
                .build();
    }
}
