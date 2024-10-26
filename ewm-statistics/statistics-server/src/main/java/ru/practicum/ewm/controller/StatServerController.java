package ru.practicum.ewm.controller;

import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.EndpointHitRequestDto;
import ru.practicum.ewm.dto.ViewStatsResponseDto;
import ru.practicum.ewm.service.ViewStatService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Validated
@Slf4j
public class StatServerController {
    private final ViewStatService service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@Valid
                     @RequestBody EndpointHitRequestDto requestDto) {
        log.debug("Save requestDto: {}", requestDto);
        service.save(requestDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsResponseDto> getStats(@NonNull
                                               @RequestParam("start")
                                               @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                               @NonNull
                                               @RequestParam("end")
                                               @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                               @RequestParam(value = "uris", required = false) List<String> uris,
                                               @RequestParam(value = "unique", defaultValue = "false") boolean unique) {
        log.debug("getStats start: {}, end: {}, uris: {}, unique: {}", start, end, uris, unique);
        return service.getStats(start, end, uris, unique);
    }
}
