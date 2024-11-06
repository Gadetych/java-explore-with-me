package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.model.Location;

@UtilityClass
public class LocationMapper {
    public Location dtoToModel(LocationDto dto) {
        return Location.builder()
                .lat(dto.getLat())
                .lon(dto.getLon())
                .build();
    }

    public LocationDto modelToDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}
