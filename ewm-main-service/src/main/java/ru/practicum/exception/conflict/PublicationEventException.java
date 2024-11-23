package ru.practicum.exception.conflict;

public class PublicationEventException extends ConflictException {
    public PublicationEventException(String message) {
        super(message);
    }

    public PublicationEventException(String message, Throwable cause) {
        super(message, cause);
    }
}
