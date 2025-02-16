package ru.practicum.controller.open;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.comment.CommentResponseDto;
import ru.practicum.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
@Slf4j
public class PublicCommentsController {
    private final CommentService service;

    @GetMapping
    public List<CommentResponseDto> findAllByEventId(@RequestParam(name = "eventId")
                                             @NotNull
                                             @Positive Long eventId,
                                                     @RequestParam(name = "from", required = false, defaultValue = "0")
                                             @PositiveOrZero Integer from,
                                                     @RequestParam(name = "size", required = false, defaultValue = "10")
                                             @PositiveOrZero Integer size) {
        log.info("==> Find all comments by event id {}", eventId);
        return service.findAllByEventId(eventId, from, size);
    }
}
