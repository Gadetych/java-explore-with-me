package ru.practicum.exception.conflict;

public class RequestModificationException extends ConflictException {
    public RequestModificationException(String message) {
        super(message);
    }

    public RequestModificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
