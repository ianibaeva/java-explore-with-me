package ru.practicum.ewm.stats.server.exception;

public class BadRequestException extends IllegalArgumentException {

    public BadRequestException(String message) {
        super(message);
    }
}
