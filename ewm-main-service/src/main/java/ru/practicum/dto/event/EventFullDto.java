package ru.practicum.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.enums.StateOfPublication;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventFullDto {
    private long id;
    private String annotation;
    private CategoryDto category;
    private int confirmedRequests;
    private UserShortDto initiator;
    private String description;
    private LocalDateTime eventDate;
    private LocalDateTime createdOn;
    private LocalDateTime publishedOn;
    private LocationDto location;
    private boolean paid;
    private int participantLimit;
    private boolean requestModeration;
    private StateOfPublication state;
    private String title;
    private int views;
}
