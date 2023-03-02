package com.arda.flightplanner.rest;

public class MaxNumberReachedException extends RestServiceException {

    private final String message;

    public MaxNumberReachedException(String message) {
        super(ErrorCode.FORBIDDEN.getSeries(),
                ErrorCode.FORBIDDEN.getCode(),
                message);
        this.message = message;

    }

    @Override
    public String getMessage() {
        return message;
    }
}
