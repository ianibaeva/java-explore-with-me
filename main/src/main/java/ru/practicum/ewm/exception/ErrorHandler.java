package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleObjectNotFoundException(final ObjectNotFoundException e) {
        log.warn("Received status 404 NOT_FOUND: {}", e.getMessage(), e);
        return new ApiError(HttpStatus.NOT_FOUND, e.getMessage(),
                "Received status 404 NOT_FOUND", LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final ConflictException e) {
        log.warn("Received status 409 CONFLICT: {}", e.getMessage(), e);
        return new ApiError(HttpStatus.CONFLICT, e.getMessage(),
                "Received status 409 CONFLICT", LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleForbiddenExceptions(final ForbiddenException e) {
        log.warn("Received status 403 FORBIDDEN: {}", e.getMessage(), e);
        return new ApiError(HttpStatus.FORBIDDEN, e.getMessage(),
                "Received status 403 FORBIDDEN",  LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleOtherExceptions(final Exception e) {
        log.warn("Received status 500 INTERNAL_SERVER_ERROR: {}", e.getMessage(), e);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(),
                "Received status 500 INTERNAL_SERVER_ERROR",  LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {

        List<String> validationErrors = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        return new ApiError(HttpStatus.BAD_REQUEST, e.getMessage(),
                "Received status 400 BAD_REQUEST",  LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestExceptions(BadRequestException e) {
        log.warn("Received status 403 FORBIDDEN: {}", e.getMessage(), e);
        return new ApiError(HttpStatus.BAD_REQUEST, e.getMessage(),
                "Received status 400 BAD_REQUEST",  LocalDateTime.now());
    }
}
