package ru.practicum.exception.validation;

public class EventModificationException extends BadRequestException {
    public EventModificationException(String message) {
        super(message);
    }
}
