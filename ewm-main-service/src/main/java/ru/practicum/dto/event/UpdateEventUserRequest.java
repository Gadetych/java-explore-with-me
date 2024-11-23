package ru.practicum.dto.event;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.enums.StateActionUser;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
@SuperBuilder
public class UpdateEventUserRequest extends BaseUpdateEventRequest {
    private StateActionUser stateAction;

    public UpdateEventUserRequest(@Length(min = 20, max = 2000) String annotation,
                                  @Positive Long category,
                                  @Length(min = 20, max = 7000) String description,
                                  LocalDateTime eventDate,
                                  LocationDto location,
                                  Boolean paid,
                                  Integer participantLimit,
                                  Boolean requestModeration,
                                  @Length(min = 3, max = 120) String title,
                                  StateActionUser stateAction) {
        super(annotation, category, description, eventDate, location, paid, participantLimit, requestModeration, title);
        this.stateAction = stateAction;
    }
}
