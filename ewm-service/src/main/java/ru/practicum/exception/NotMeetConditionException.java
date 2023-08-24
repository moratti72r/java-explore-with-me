package ru.practicum.exception;

public class NotMeetConditionException extends RuntimeException {
    private final String message;

    public NotMeetConditionException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
