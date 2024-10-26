package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.EndpointHitRequestDto;
import ru.practicum.ewm.dto.ViewStatsResponseDto;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.ViewStats;

@UtilityClass
public class ViewStatMapper {

    public EndpointHit dtoRequestToModel(EndpointHitRequestDto dto) {
        EndpointHit model = new EndpointHit();
        model.setApp(dto.getApp());
        model.setUri(dto.getUri());
        model.setIp(dto.getIp());
        model.setTimestamp(dto.getTimestamp());
        return model;
    }

    public ViewStatsResponseDto modelToResponseDto(ViewStats model) {
        ViewStatsResponseDto dto = new ViewStatsResponseDto();
        dto.setApp(model.getApp());
        dto.setUri(model.getUri());
        dto.setHits(model.getHits());
        return dto;
    }
}
