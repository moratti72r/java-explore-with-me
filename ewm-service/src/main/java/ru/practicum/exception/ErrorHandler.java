package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleValidation(ConstraintViolationException e) {
        log.warn("Получен статус 409 Conflict {}", e.getMessage(), e);
        return Map.of("status", HttpStatus.CONFLICT.name(),
                "reason", "Integrity constraint has been violated.",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().format(FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleValidation(NotFoundException e) {
        log.warn("Получен статус 404 Not found {}", e.getMessage(), e);
        return Map.of("status", HttpStatus.NOT_FOUND.name(),
                "reason", "The required object was not found.",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().format(FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleValidation(NotMeetConditionException e) {
        log.warn("Получен статус 403 Forbidden {}", e.getMessage(), e);
        return Map.of("status", HttpStatus.FORBIDDEN.name(),
                "reason", "For the requested operation the conditions are not met.",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().format(FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidation(MethodArgumentNotValidException e) {
        log.warn("Получен статус 400 Bad request {}", e.getMessage(), e);
        return Map.of("status", HttpStatus.BAD_REQUEST.name(),
                "reason", "Incorrectly made request",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().format(FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidation(ValidationException e) {
        log.warn("Получен статус 400 Bad request {}", e.getMessage(), e);
        return Map.of("status", HttpStatus.BAD_REQUEST.name(),
                "reason", "Incorrectly made request",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().format(FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleInternalServerError(Throwable e) {
        log.warn("Получен статус 500 Internal Server Error {}", e.getMessage(), e);
        return Map.of("status", HttpStatus.INTERNAL_SERVER_ERROR.name(),
                "reason", "INTERNAL SERVER ERROR",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().format(FORMATTER));
    }
}
