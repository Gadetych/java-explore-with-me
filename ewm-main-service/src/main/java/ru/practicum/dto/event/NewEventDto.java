package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.dto.location.LocationDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewEventDto {
    @NotNull
    @Length(min = 20, max = 2000)
    private String annotation;
    @Positive
    private long category;
    @NotNull
    @Length(min = 20, max = 2000)
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @NotNull
    private LocationDto location;
    @Builder.Default
    private boolean paid = false;
    @Builder.Default
    private int participantLimit = 0;
    @Builder.Default
    private boolean requestModeration = true;
    @NotNull
    @Length(min = 3, max = 120)
    private String title;

    @AssertTrue
    private boolean isValidEventDate() {
        return eventDate.isAfter(LocalDateTime.now().plusHours(2));
    }
}
