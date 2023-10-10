package ru.practicum.ewm.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class ApiError {
    private HttpStatus status;
    private String message;
    private String reason;
    private LocalDateTime timestamp;
}
