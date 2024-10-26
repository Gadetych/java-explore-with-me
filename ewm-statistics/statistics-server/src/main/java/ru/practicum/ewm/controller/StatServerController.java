package ru.practicum.ewm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.EndpointHitRequestDto;
import ru.practicum.ewm.dto.ViewStatsResponseDto;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
public class StatServerController {
    @PostMapping("/hits")
    @ResponseStatus(HttpStatus.CREATED)
    public ViewStatsResponseDto create(@Valid
                                       @RequestBody EndpointHitRequestDto requestDto) {
        return null;
    }
}
