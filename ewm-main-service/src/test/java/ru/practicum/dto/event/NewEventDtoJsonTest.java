package ru.practicum.dto.event;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import ru.practicum.dto.location.LocationDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@JsonTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class NewEventDtoJsonTest {
    private final JacksonTester<NewEventDto> json;
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void serialize() throws IOException {
        NewEventDto newEventDto = NewEventDto.builder()
                .annotation("annotation")
                .category(1)
                .description("description")
                .eventDate(LocalDateTime.of(2030, 11, 5, 2, 4, 30))
                .location(LocationDto.builder()
                        .lat(55.331)
                        .lon(5.112)
                        .build())
                .title("title")
                .build();
        JsonContent<NewEventDto> result = json.write(newEventDto);
        assertThat(result.getJson()).isEqualTo("{\"annotation\":\"annotation\",\"category\":1,\"description\":\"description\",\"eventDate\":\"2030-11-05 02:04:30\",\"location\":{\"id\":null,\"lat\":55.331,\"lon\":5.112},\"paid\":false,\"participantLimit\":0,\"requestModeration\":true,\"title\":\"title\"}");
    }

    @Test
    void deserialize() throws IOException {
        String eventJson = "{\"annotation\":\"annotation\",\"category\":1,\"description\":\"description\",\"eventDate\":\"2030-11-05 02:04:30\",\"location\":{\"lat\":55.331,\"lon\":5.112},\"paid\":false,\"participantLimit\":0,\"requestModeration\":true,\"title\":\"title\"}";
        ObjectContent<NewEventDto> result = json.parse(eventJson);
        NewEventDto newEventDto = NewEventDto.builder()
                .annotation("annotation")
                .category(1)
                .description("description")
                .eventDate(LocalDateTime.of(2030, 11, 5, 2, 4, 30))
                .location(LocationDto.builder()
                        .lat(55.331)
                        .lon(5.112)
                        .build())
//                .requestModeration(true)
                .title("title")
                .build();

        assertThat(result).isEqualTo(newEventDto);
    }

    @Test
    void deserialize_whenDefaultValuesIsEmpty_thenTrue() throws IOException {
        String eventJson = "{\"annotation\":\"annotation\",\"category\":1,\"description\":\"description\",\"eventDate\":\"2030-11-05 02:04:30\",\"location\":{\"lat\":55.331,\"lon\":5.112},\"title\":\"title\"}";
        ObjectContent<NewEventDto> result = json.parse(eventJson);
        NewEventDto newEventDto = NewEventDto.builder()
                .annotation("annotation")
                .category(1)
                .description("description")
                .eventDate(LocalDateTime.of(2030, 11, 5, 2, 4, 30))
                .location(LocationDto.builder()
                        .lat(55.331)
                        .lon(5.112)
                        .build())
                .requestModeration(true)
                .title("title")
                .build();

        assertThat(result.getObject().isRequestModeration()).isTrue();
        assertThat(result).isEqualTo(newEventDto);
    }

    @Test
    void deserialize_whenEventDateNotValid_thenReturnException() throws IOException {
        String eventJson = "{\"annotation\":\"annotationsdfwfwfafsadfdsfacewcwcwefasdfasdfwaefawfEWFAsdfsfw\",\"category\":1,\"description\":\"descriptionannotationsdfwfwfafsadfdsfacewcwcwefasdfasdfwaefawfEWFAsdfsfw\",\"eventDate\":\"2000-11-05 02:04:30\",\"location\":{\"lat\":55.331,\"lon\":5.112},\"title\":\"title\"}";
        NewEventDto result = json.parse(eventJson).getObject();
        Set<ConstraintViolation<NewEventDto>> violations = validator.validate(result);

        assertThat(violations).hasSize(1);
    }
}