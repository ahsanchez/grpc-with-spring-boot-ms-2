package com.orange.pocs.grpc.restapi.config;

import com.orange.pocs.grpc.restapi.exception.NotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.text.SimpleDateFormat;
import java.util.Date;


@RestControllerAdvice
@Log4j2
public class RestExceptionHandler {

    private static final String DATE_FORMATTER = "yyyy-MM-dd HH:mm:ss";

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> notFoundExceptionHandler(final NotFoundException exception) {
        log.error(exception.getMessage(), exception);
        final ErrorResponse response = createErrorResponse(HttpStatus.NOT_FOUND.value(), exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    private ErrorResponse createErrorResponse(final int code, final String message) {
        final String date = new SimpleDateFormat(DATE_FORMATTER).format(new Date());
        return new ErrorResponse(code, message, date);
    }
}