package com.example.authapi.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage();

        if (message.contains("401")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", message));
        } else if (message.contains("403")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", message));
        } else if (message.contains("400")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", message));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", message));
        }
    }
}