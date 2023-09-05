package ru.practicum.exception;

public class MethodArgumentNotMeetLogicAppException extends RuntimeException {

    private final String message;

    public MethodArgumentNotMeetLogicAppException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
