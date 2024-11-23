package ru.practicum.exception.conflict;

public class DeletingCategoryWithLinkedEventsException extends ConflictException {

    public DeletingCategoryWithLinkedEventsException(String message) {
        super(message);
    }

    public DeletingCategoryWithLinkedEventsException(String message, Throwable cause) {
        super(message, cause);
    }
}
