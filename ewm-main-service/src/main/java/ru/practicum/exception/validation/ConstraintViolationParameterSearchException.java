package ru.practicum.exception.validation;

public class ConstraintViolationParameterSearchException extends BadRequestException {
    public ConstraintViolationParameterSearchException(String message) {
        super(message);
    }
}
