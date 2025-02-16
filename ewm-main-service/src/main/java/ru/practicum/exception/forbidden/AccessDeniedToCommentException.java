package ru.practicum.exception.forbidden;

public class AccessDeniedToCommentException extends ForbiddenException {
    public AccessDeniedToCommentException(String message) {
        super(message);
    }
}
