package com.bismillah.InventoryManagementSystem.exception;

import com.bismillah.InventoryManagementSystem.dto.Response;
import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Builder
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler (Exception.class)

    public ResponseEntity<Response> handleAllExceptions (Exception ex) {

        Response response = Response.builder()

                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())

                .message(ex.getMessage())

                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler (NotFoundException.class)

    public ResponseEntity<Response> handleNotFoundException (NotFoundException ex) {

        Response response = Response.builder()

                .status(HttpStatus.NOT_FOUND.value())

                .message(ex.getMessage())

                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler (NameValueRequiredException.class)

    public ResponseEntity<Response> handleNameValueRequiredException (NameValueRequiredException ex) {

        Response response = Response.builder()

                .status(HttpStatus.BAD_REQUEST.value())

                .message(ex.getMessage())

                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler (InvalidCredentialsException.class)

    public ResponseEntity<Response> handleInvalidCredentialsException (InvalidCredentialsException ex) {

        Response response = Response.builder()

                .status(HttpStatus.BAD_REQUEST.value())

                .message(ex.getMessage())

                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler (org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<Response> handleDataIntegrityViolationException (org.springframework.dao.DataIntegrityViolationException ex) {
        Response response = Response.builder()
                .status(HttpStatus.CONFLICT.value())
                .message("Data integrity violation: " + ex.getMostSpecificCause().getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler (DeleteConstraintException.class)
    public ResponseEntity<Response> handleDeleteConstraintException (DeleteConstraintException ex) {
        Response response = Response.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
