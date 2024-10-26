package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.EndpointHitRequestDto;
import ru.practicum.ewm.dto.ViewStatsResponseDto;
import ru.practicum.ewm.mapper.ViewStatMapper;
import ru.practicum.ewm.model.ViewStats;
import ru.practicum.ewm.repository.ViewStatRepository;

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
        log.debug("<== Save endpoint hit: {}", requestDto);
    }

    @Override
    public List<ViewStatsResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.debug("==> Get endpoint hits start: {}, end: {}, uris: {}, unique: {}", start, end, uris, unique);
        List<ViewStats> resultModel;
        if (unique) {
            resultModel = repository.findViewStatsByUriForUniqueIP(start, end, uris);
        } else {
            resultModel = repository.findViewStatsByUri(start, end, uris);
        }
        log.debug("<== Get endpoint hits result: {}", resultModel);
        return resultModel.stream()
                .map(ViewStatMapper::modelToResponseDto)
                .toList();
    }
}
