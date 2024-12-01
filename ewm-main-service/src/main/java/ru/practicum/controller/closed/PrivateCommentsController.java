package ru.practicum.controller.closed;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;
import ru.practicum.service.CommentService;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/users/{userId}/comments")
@Slf4j
public class PrivateCommentsController {
    private final CommentService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(@PathVariable @NotNull
                             @Positive Long userId, @RequestParam("eventId") @NotNull
                             @Positive
                             Long eventId,
                             @RequestBody
                             @Valid NewCommentDto requestBody) {
        log.info("==> Created new comment: user id {}, event id {}, request body {}", userId, eventId, requestBody);
        return service.create(userId, eventId, requestBody);
    }

    @PatchMapping("/{comId}")
    public CommentDto change(@PathVariable @NotNull
                             @Positive Long userId, @PathVariable @NotNull
                             @Positive Long comId,
                             @RequestBody
                             @Valid UpdateCommentDto requestBody) {
        log.info("==> Change comment: user id {}, comment id {}, request body {}", userId, comId, requestBody);
        return service.change(userId, comId, requestBody);
    }

    @DeleteMapping("/{comId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @NotNull
                       @Positive Long userId, @PathVariable @NotNull
                       @Positive Long comId) {
        log.info("==> Delete comment: user id {}, comment id {}", userId, comId);
        service.delete(userId, comId);
    }
}
