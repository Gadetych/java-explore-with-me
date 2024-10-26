package ru.practicum.ewm;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.ewm.dto.EndpointHitRequestDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationDtoTest {
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldTrueViolationsIsEmpty() {
        EndpointHitRequestDto requestDto = EndpointHitRequestDto.builder()
                .app("ewm-main-service")
                .uri("/events/1")
                .ip("192.163.0.1")
                .timestamp(LocalDateTime.parse("2022-09-06 11:00:23", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
        Set<ConstraintViolation<EndpointHitRequestDto>> violations = validator.validate(requestDto);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldTrueFieldValidationDto() {
        EndpointHitRequestDto requestDto = EndpointHitRequestDto.builder().build();
        Set<ConstraintViolation<EndpointHitRequestDto>> violations = validator.validate(requestDto);
        assertEquals(4, violations.size());
    }
}
