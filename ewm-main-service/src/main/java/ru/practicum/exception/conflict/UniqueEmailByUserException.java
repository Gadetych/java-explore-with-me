package ru.practicum.exception.conflict;

public class UniqueEmailByUserException extends ConflictException {
    public UniqueEmailByUserException(String message) {
        super(message);
    }

    public UniqueEmailByUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
