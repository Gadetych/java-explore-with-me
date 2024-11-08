package ru.practicum.exception;

public class RequestModificationException extends RuntimeException {
    public RequestModificationException(String message) {
        super(message);
    }

    public RequestModificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
