package com.example.petclinic;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Maps bean validation violations to HTTP 400 responses.
 */
@ControllerAdvice
class ConstraintViolationExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles constraint violations raised by validation.
     *
     * @param e constraint violation exception
     * @param request current request
     * @return response with validation message
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException e, WebRequest request) {
        return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
