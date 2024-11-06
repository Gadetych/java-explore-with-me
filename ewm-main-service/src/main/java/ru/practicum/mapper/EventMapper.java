package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class EventMapper {
    public EventShortDto modelToShortDto(Event model) {
        return EventShortDto.builder()
                .id(model.getId())
                .annotation(model.getAnnotation())
                .category(CategoryMapper.modelToDto(model.getCategory()))
//                .confirmedRequests(?)
                .eventDate(model.getEventDate())
                .initiator(UserMapper.modelToUserShortDto(model.getInitiator()))
                .paid(model.isPaid())
                .title(model.getTitle())
//                .views(?)
                .build();
    }

    public Event newEventDtoToModel(NewEventDto dto, CategoryDto categoryDto, User user, Location location) {
        LocalDateTime now = LocalDateTime.now();
        return Event.builder()
                .annotation(dto.getAnnotation())
//              TODO:  проверить как сохранилась категория
                .category(CategoryMapper.dtoToModel(categoryDto))
//              TODO: проверить как сохранился пользователь
                .initiator(user)
                .description(dto.getDescription())
                .eventDate(dto.getEventDate())
                .createdOn(now)
//                TODO: проверить сохранилась ли в БД геопозиция
                .location(location)
                .paid(dto.isPaid())
                .participantLimit(dto.getParticipantLimit())
                .requestModeration(dto.isRequestModeration())
                .title(dto.getTitle())
                .build();
    }

    public static EventFullDto modelToFullDto(Event model) {
        return EventFullDto.builder()
                .id(model.getId())
                .annotation(model.getAnnotation())
                .category(CategoryMapper.modelToDto(model.getCategory()))
//                .confirmedRequests()
                .initiator(UserMapper.modelToUserShortDto(model.getInitiator()))
                .description(model.getDescription())
                .eventDate(model.getEventDate())
                .createdOn(model.getCreatedOn())
                .publishedOn(model.getPublishedOn())
                .location(LocationMapper.modelToDto(model.getLocation()))
                .paid(model.isPaid())
                .participantLimit(model.getParticipantLimit())
                .requestModeration(model.isRequestModeration())
                .state(model.getState())
                .title(model.getTitle())
//                .views()
                .build();
    }
}
