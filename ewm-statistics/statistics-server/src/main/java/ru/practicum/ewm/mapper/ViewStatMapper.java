package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.stats.common.dto.EndpointHitRequestDto;

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
}
