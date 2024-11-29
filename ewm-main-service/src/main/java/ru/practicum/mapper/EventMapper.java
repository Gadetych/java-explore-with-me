package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.EventTitleDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class EventMapper {
    public EventShortDto modelToShortDto(Event model, long confirmedRequests, long views) {
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
                .participantLimit(model.getParticipantLimit())
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

    public EventFullDto modelToFullDto(Event model, long confirmedRequests, long views) {
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

    public Event fullDtoToModel(EventFullDto dto) {
        return Event.builder()
                .id(dto.getId())
                .annotation(dto.getAnnotation())
                .category(CategoryMapper.dtoToModel(dto.getCategory()))
                .initiator(UserMapper.shortDtoToModel(dto.getInitiator()))
                .description(dto.getDescription())
                .eventDate(dto.getEventDate())
                .createdOn(dto.getCreatedOn())
                .publishedOn(dto.getPublishedOn())
                .location(LocationMapper.dtoToModel(dto.getLocation()))
                .paid(dto.isPaid())
                .participantLimit(dto.getParticipantLimit())
                .requestModeration(dto.isRequestModeration())
                .state(dto.getState())
                .title(dto.getTitle())
                .build();
    }

    public EventShortDto fullDtoToShortDto(EventFullDto eventFullDto) {
        return EventShortDto.builder()
                .id(eventFullDto.getId())
                .annotation(eventFullDto.getAnnotation())
                .category(eventFullDto.getCategory())
                .confirmedRequests(eventFullDto.getConfirmedRequests())
                .eventDate(eventFullDto.getEventDate())
                .initiator(eventFullDto.getInitiator())
                .paid(eventFullDto.isPaid())
                .title(eventFullDto.getTitle())
                .views(eventFullDto.getViews())
                .participantLimit(eventFullDto.getParticipantLimit())
                .build();
    }

    public static EventTitleDto modelToEventTitleDto(Event event) {
        return EventTitleDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .build();
    }
}
