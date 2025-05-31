package com.example.authapi.controller;

import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.example.authapi.model.User;
import com.example.authapi.service.AuthService;

@RestController
public class AuthController {

    @Autowired
    private AuthService service;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        if (user.getUserId() == null || user.getPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing user_id or password"));
        }
        try {
            User created = service.signup(user.getUserId(), user.getPassword());
            return ResponseEntity.ok(Map.of(
                    "message", "Account successfully created",
                    "user", Map.of("user_id", created.getUserId(), "nickname", created.getNickname())
            ));
        } catch (RuntimeException e) {
            return handleError(e);
        }
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUser(@RequestHeader(value = "Authorization", required = false) String auth,
                                     @PathVariable String userId) {
        try {
            User user = service.getUser(auth, userId);
            return ResponseEntity.ok(Map.of(
                    "message", "User details by user_id",
                    "user", Map.of(
                            "user_id", user.getUserId(),
                            "nickname", user.getNickname(),
                            "comment", user.getComment())
            ));
        } catch (RuntimeException e) {
            return handleError(e);
        }
    }

    @PatchMapping("/users/{userId}")
    public ResponseEntity<?> update(@RequestHeader(value = "Authorization", required = false) String auth,
                                    @PathVariable String userId,
                                    @RequestBody Map<String, String> updates) {
        try {
            User user = service.updateUser(auth, userId, updates.get("nickname"), updates.get("comment"));
            return ResponseEntity.ok(Map.of(
                    "message", "User successfully updated",
                    "user", Map.of(
                            "nickname", user.getNickname(),
                            "comment", user.getComment())
            ));
        } catch (RuntimeException e) {
            return handleError(e);
        }
    }

    @PostMapping("/close")
    public ResponseEntity<?> close(@RequestHeader(value = "Authorization", required = false) String auth) {
        try {
            String userId = extractUserId(auth);
            service.deleteUser(auth, userId);
            return ResponseEntity.ok(Map.of("message", "Account and user deleted"));
        } catch (RuntimeException e) {
            return handleError(e);
        }
    }

    private String extractUserId(String auth) {
        String base64Credentials = auth.substring("Basic ".length());
        String[] values = new String(Base64.getDecoder().decode(base64Credentials)).split(":");
        return values[0];
    }

    private ResponseEntity<Map<String, String>> handleError(RuntimeException e) {
        if (e.getMessage().contains("Authentication")) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentication Failed"));
        } else if (e.getMessage().contains("Permission")) {
            return ResponseEntity.status(403).body(Map.of("error", "No Permission for this user"));
        } else if (e.getMessage().contains("Invalid signup")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Account creation failed"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}