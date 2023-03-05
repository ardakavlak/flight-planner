package com.arda.flightplanner.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;


@RequiredArgsConstructor
@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler {
    private final MessageSource messageSource;

    private static final String COMMA = ", ";

    @ExceptionHandler(RestServiceException.class)
    public ResponseEntity<Response<ResponseError>> handleRestServiceException(RestServiceException restServiceException) {
        log.error("handler: " + restServiceException);
        return ResponseEntity
                .status(findHttpStatus(restServiceException.getSeries(), restServiceException.getCode()))
                .body(Response.error(new ResponseError(getMessage(restServiceException.getMessage()))));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Response<ResponseError>> handleMethodArgumentNotValid
            (MethodArgumentNotValidException methodArgumentNotValidException) {
        log.error("handler: " + methodArgumentNotValidException);
        var errorMessage = methodArgumentNotValidException
                .getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .map(this::getMessage)
                .collect(Collectors.joining(COMMA));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Response.error(new ResponseError(errorMessage)));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Response<ResponseError>> handleGeneralException(RuntimeException exception) {
        log.error("handler: " + exception);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error(new ResponseError(getMessage("exceptionHandler.exception.message"))));
    }

    private HttpStatus findHttpStatus(int series, int code) {
        HttpStatus status = HttpStatus.resolve(series + code);
        return status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String getMessage(String messageKey) {
        try {
            return messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            return "";
        }
    }
}
