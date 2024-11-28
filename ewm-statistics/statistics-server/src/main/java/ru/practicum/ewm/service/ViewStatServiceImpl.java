package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.mapper.ViewStatMapper;
import ru.practicum.ewm.repository.ViewStatRepository;
import ru.practicum.ewm.stats.common.dto.EndpointHitRequestDto;
import ru.practicum.ewm.stats.common.dto.ViewStatsResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ViewStatServiceImpl implements ViewStatService {
    private final ViewStatRepository repository;

    @Override
    public void save(EndpointHitRequestDto requestDto) {
        log.debug("==> Save endpoint hit: {}", requestDto);
        repository.save(ViewStatMapper.dtoRequestToModel(requestDto));
        log.debug("<== Saved endpoint hit: {}", requestDto);
    }

    @Override
    public List<ViewStatsResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.debug("==> Get endpoint hits start: {}, end: {}, uris: {}, unique: {}", start, end, uris, unique);
        List<ViewStatsResponseDto> responseDtoList;
        if (unique) {
            responseDtoList = repository.findViewStatsByUriForUniqueIP(start, end, uris);
        } else {
            responseDtoList = repository.findViewStatsByUri(start, end, uris);
        }
        log.debug("<== Got endpoint hits result: {}", responseDtoList);
        return responseDtoList;
    }
}
