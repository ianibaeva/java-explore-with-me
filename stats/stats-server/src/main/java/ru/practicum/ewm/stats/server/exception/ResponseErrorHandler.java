package ru.practicum.ewm.stats.server.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ResponseErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestExceptions(BadRequestException e) {
        log.warn("Received status 400 BAD_REQUEST: {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }
}
