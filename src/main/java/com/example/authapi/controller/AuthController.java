// package: com.example.authapi.controller

package com.example.authapi.controller;

import org.apache.logging.log4j.message.SimpleMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.authapi.dto.ErrorResponse;
import com.example.authapi.dto.SignupRequest;
import com.example.authapi.dto.UpdateRequest;
import com.example.authapi.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        try {
            return ResponseEntity.ok(authService.signup(request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Account creation failed", e.getMessage()));
        }
    }

    @GetMapping("/users/{user_id}")
    public ResponseEntity<?> getUser(@PathVariable("user_id") String userId,
                                     @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            return ResponseEntity.ok(authService.getUser(userId, authHeader));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SimpleMessage("Authentication Failed"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new SimpleMessage("No User found"));
        }
    }

    @PatchMapping("/users/{user_id}")
    public ResponseEntity<?> updateUser(@PathVariable("user_id") String userId,
                                        @RequestHeader(value = "Authorization", required = false) String authHeader,
                                        @RequestBody UpdateRequest request) {
        try {
            return ResponseEntity.ok(authService.updateUser(userId, authHeader, request));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SimpleMessage("Authentication Failed"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("User updation failed", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new SimpleMessage("No Permission for Update"));
        }
    }

    @PostMapping("/close")
    public ResponseEntity<?> closeAccount(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            String msg = authService.deleteUser(authHeader);
            return ResponseEntity.ok(new SimpleMessage(msg));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SimpleMessage("Authentication Failed"));
        }
    }
}
