package com.arda.flightplanner.rest;

public class FailedToAcquireLockException extends RestServiceException {

    private final String message;

    public FailedToAcquireLockException(String message) {
        super(ErrorCode.REQUEST_TIMEOUT.getSeries(),
                ErrorCode.REQUEST_TIMEOUT.getCode(),
                message);
        this.message = message;

    }

    @Override
    public String getMessage() {
        return message;
    }
}
