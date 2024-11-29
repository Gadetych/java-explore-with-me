package ru.practicum.controller.open;

import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
@Slf4j
public class PublicCommentsController {
    private final CommentService service;

    @GetMapping("/{eventId}")
    public List<CommentDto> findAllByEventId(@PathVariable
                                             @NonNull
                                             @Positive Long eventId,
                                             @RequestParam(name = "from", defaultValue = "0")
                                             @Positive Integer from,
                                             @RequestParam(name = "size", defaultValue = "10")
                                             @Positive Integer size) {
        return service.findAllByEventId(eventId, from, size);
    }
}
