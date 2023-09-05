package ru.practicum.exception;

public class NotMeetLogicAppException extends RuntimeException {

    private final String message;

    public NotMeetLogicAppException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
