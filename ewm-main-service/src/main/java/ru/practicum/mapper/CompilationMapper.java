package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.model.Compilation;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {
    public Compilation newDtoToModel(NewCompilationDto requestBody, List<EventFullDto> events) {
        return Compilation.builder()
                .pinned(requestBody.isPinned())
                .title(requestBody.getTitle())
                .events(events.stream().map(EventMapper::fullDtoToModel).toList())
                .build();
    }

    public CompilationDto modelToDto(Compilation model, List<EventFullDto> eventFullDtoList) {
        return CompilationDto.builder()
                .id(model.getId())
                .pinned(model.isPinned())
                .title(model.getTitle())
                .events(eventFullDtoList.stream()
                        .map(EventMapper::fullDtoToShortDto)
                        .collect(Collectors.toList()))
                .build();
    }
}
