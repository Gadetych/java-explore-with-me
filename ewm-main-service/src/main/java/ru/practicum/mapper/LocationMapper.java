package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.model.Location;

@UtilityClass
public class LocationMapper {
    public Location dtoToModel(LocationDto dto) {
        return Location.builder()
                .id(dto.getId())
                .lat(dto.getLat())
                .lon(dto.getLon())
                .build();
    }

    public LocationDto modelToDto(Location location) {
        return LocationDto.builder()
                .id(location.getId())
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}
