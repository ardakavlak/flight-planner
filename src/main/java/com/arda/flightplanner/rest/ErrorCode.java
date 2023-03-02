package com.arda.flightplanner.rest;

import lombok.Getter;

@Getter
public enum ErrorCode {


    BAD_CREDENTIALS(400, 0),
    UNAUTHORIZED(400, 1),
    FORBIDDEN(400, 3),
    RESOURCE_NOT_FOUND(400, 4),
    INTERNAL_SERVER_ERROR(500, 0),
    REQUEST_TIMEOUT(408, 5);

    private final int series;
    private final int code;

    ErrorCode(int series, int code) {
        this.series = series;
        this.code = code;
    }

}
