package com.example.authapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.authapi.dto.ErrorResponse;
import com.example.authapi.dto.SimpleMessage;

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
	            .body(new SimpleMessage(message)); // ensures "message" property
	}
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
	    String errorMsg = ex.getBindingResult()
	            .getFieldErrors()
	            .stream()
	            .map(field -> field.getField() + " " + field.getDefaultMessage())
	            .findFirst()
	            .orElse("Validation error");

	    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	            .body(new ErrorResponse("Account creation failed", errorMsg));
	}
	
}
