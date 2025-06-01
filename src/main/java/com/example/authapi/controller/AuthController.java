package com.example.authapi.controller;

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
import com.example.authapi.dto.SignupResponse;
import com.example.authapi.dto.SimpleMessage;
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
        SignupResponse response = authService.signup(request);
        return ResponseEntity.ok(response);
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
            if ("Authentication Failed".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new SimpleMessage("Authentication Failed"));
            } else if ("No Permission for Update".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new SimpleMessage("No Permission for Update"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new SimpleMessage(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("User updation failed", e.getMessage()));
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
