package ru.practicum.ewm.exception;

public class TimestampRangeException extends BadRequestException {
    public TimestampRangeException() {
        super();
    }

    public TimestampRangeException(String message) {
        super(message);
    }

    public TimestampRangeException(String message, Throwable cause) {
        super(message, cause);
    }
}
