package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class EventMapper {
    public EventShortDto modelToShortDto(Event model, int confirmedRequests, long views) {
        return EventShortDto.builder()
                .id(model.getId())
                .annotation(model.getAnnotation())
                .category(CategoryMapper.modelToDto(model.getCategory()))
                .confirmedRequests(confirmedRequests)
                .eventDate(model.getEventDate())
                .initiator(UserMapper.modelToUserShortDto(model.getInitiator()))
                .paid(model.isPaid())
                .title(model.getTitle())
                .views(views)
                .build();
    }


    public Event newEventDtoToModel(NewEventDto dto, Category category, User user) {
        LocalDateTime now = LocalDateTime.now();
        return Event.builder()
                .annotation(dto.getAnnotation())
                .category(category)
                .initiator(user)
                .description(dto.getDescription())
                .eventDate(dto.getEventDate())
                .createdOn(now)
                .location(LocationMapper.dtoToModel(dto.getLocation()))
                .paid(dto.isPaid())
                .participantLimit(dto.getParticipantLimit())
                .requestModeration(dto.isRequestModeration())
                .title(dto.getTitle())
                .build();
    }

    public static EventFullDto modelToFullDto(Event model, int confirmedRequests, long views) {
        return EventFullDto.builder()
                .id(model.getId())
                .annotation(model.getAnnotation())
                .category(CategoryMapper.modelToDto(model.getCategory()))
                .confirmedRequests(confirmedRequests)
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
                .views(views)
                .build();
    }
}
