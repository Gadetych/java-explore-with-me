package ru.practicum.exception.conflict;

public class UniqueNameCategoriesException extends ConflictException {
    public UniqueNameCategoriesException(String message) {
        super(message);
    }

    public UniqueNameCategoriesException(String message, Throwable cause) {
        super(message, cause);
    }
}
