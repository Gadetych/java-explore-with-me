package ru.practicum.controller.handler;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.stats.common.dto.ApiError;
import ru.practicum.exception.conflict.ConflictException;
import ru.practicum.exception.forbidden.ForbiddenException;
import ru.practicum.exception.not_found.NotFoundException;
import ru.practicum.exception.validation.BadRequestException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class MainServiceHandlerController {
    private final StringWriter sw = new StringWriter();
    private final PrintWriter pw = new PrintWriter(sw);

    @ExceptionHandler({DataAccessException.class, NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(RuntimeException e) {
        log.info("404 {}", e.getMessage(), e);
        e.printStackTrace(pw);
        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setStatus(HttpStatus.NOT_FOUND.toString());
        apiError.setReason("The required object was not found.");
        apiError.setTimestamp(LocalDateTime.now());
        return apiError;
    }

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentNotValidException.class, DataIntegrityViolationException.class,
            MissingServletRequestParameterException.class, BadRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(RuntimeException e) {
        log.info("400 {}", e.getMessage(), e);
        e.printStackTrace(pw);
        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setStatus(HttpStatus.BAD_REQUEST.toString());
        apiError.setReason("Incorrectly made request.");
        apiError.setTimestamp(LocalDateTime.now());
        return apiError;
    }

    @ExceptionHandler({ForbiddenException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleForbiddenRequest(RuntimeException e) {
        log.info("403 {}", e.getMessage(), e);
        e.printStackTrace(pw);
        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setStatus(HttpStatus.FORBIDDEN.toString());
        apiError.setReason("Not access.");
        apiError.setTimestamp(LocalDateTime.now());
        return apiError;
    }

    @ExceptionHandler({ConflictException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleUserAlreadyExist(RuntimeException e) {
        log.info("409 {}", e.getMessage(), e);
        e.printStackTrace(pw);
        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setStatus(HttpStatus.CONFLICT.toString());
        apiError.setReason("Integrity constraint has been violated.");
        apiError.setTimestamp(LocalDateTime.now());
        return apiError;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleServerError(final Exception e) {
        log.info("500 {}", e.getMessage(), e);
        e.printStackTrace(pw);
        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        apiError.setReason("Internal Server Error");
        apiError.setTimestamp(LocalDateTime.now());
        return apiError;
    }
}
