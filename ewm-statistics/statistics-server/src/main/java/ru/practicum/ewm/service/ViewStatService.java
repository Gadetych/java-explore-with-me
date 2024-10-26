package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.EndpointHitRequestDto;
import ru.practicum.ewm.dto.ViewStatsResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ViewStatService {

    void save(EndpointHitRequestDto requestDto);

    List<ViewStatsResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
