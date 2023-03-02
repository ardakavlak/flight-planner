package com.arda.flightplanner.rest;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public final class Response<T> implements Serializable {

    private T payload;

    private ResponseError error;

    private Response(T payload) {
        this.payload = payload;
    }

    private Response(ResponseError error) {
        this.error = error;
    }

    public static <T> Response<T> success(T data) {
        return new Response<>(data);
    }

    public static <T> Response<T> error(ResponseError error) {
        return new Response<>(error);
    }

}
