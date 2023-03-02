package com.arda.flightplanner.rest;

public abstract class RestServiceException extends RuntimeException {

    private final int series;
    private final int code;

    RestServiceException(int series, int code, String message) {
        super(message);
        this.code = code;
        this.series = series;
    }

    public int getCode() {
        return code;
    }

    public int getSeries() {
        return series;
    }
}
