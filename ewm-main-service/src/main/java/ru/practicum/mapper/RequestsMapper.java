package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.model.Request;

@UtilityClass
public class RequestsMapper {
    public ParticipationRequestDto modelToDto(Request model) {
        return ParticipationRequestDto.builder()
                .id(model.getId())
                .requester(model.getRequester().getId())
                .event(model.getEvent().getId())
                .created(model.getCreated())
                .status(model.getStatus())
                .build();
    }
}
