package com.example.authapi.exception;

import com.example.authapi.dto.ErrorResponse;
import com.example.authapi.dto.SimpleMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<?> handleSecurity(SecurityException ex) {
        String message = ex.getMessage();
        HttpStatus status;

        if ("Authentication Failed".equals(message)) {
            status = HttpStatus.UNAUTHORIZED; // 401
        } else if ("No Permission for Update".equals(message)) {
            status = HttpStatus.FORBIDDEN; // 403
        } else {
            status = HttpStatus.BAD_REQUEST;
        }

        return ResponseEntity.status(status)
                .body(new SimpleMessage(message)); // returns {"message": "..."}
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Account creation failed", "required user_id and password"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        String message = ex.getMessage();
        if ("already same user_id is used".equals(message)) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Account creation failed", message));
        }
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("Account creation failed", "Invalid request"));
    }
}
