package com.example.authapi.controller;

import java.util.Base64;
import java.util.List;
import java.util.Map;

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
	private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        User created = service.signup(user.getUserId(), user.getPassword());
        return ResponseEntity.ok().body(Map.of(
                "message", "Account successfully created",
                "user", Map.of("user_id", created.getUserId(), "nickname", created.getNickname())
        ));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUser(@RequestHeader("Authorization") String auth,
                                     @PathVariable String userId) {
        User user = service.getUser(auth, userId);
        return ResponseEntity.ok().body(Map.of(
                "message", "User details by user_id",
                "user", Map.of("user_id", user.getUserId(), "nickname", user.getNickname(), "comment", user.getComment())
        ));
    }

    @PatchMapping("/users/{userId}")
    public ResponseEntity<?> update(@RequestHeader("Authorization") String auth,
                                    @PathVariable String userId,
                                    @RequestBody Map<String, String> updates) {
        User user = service.updateUser(auth, userId, updates.get("nickname"), updates.get("comment"));
        return ResponseEntity.ok().body(Map.of(
                "message", "User successfully updated",
                "recipe", List.of(Map.of("nickname", user.getNickname(), "comment", user.getComment()))
        ));
    }

    @PostMapping("/close")
    public ResponseEntity<?> close(@RequestHeader("Authorization") String auth) {
        String userId = extractUserId(auth);
        service.deleteUser(auth, userId);
        return ResponseEntity.ok().body(Map.of("message", "Account and user deleted"));
    }

    private String extractUserId(String auth) {
        String base64Credentials = auth.substring("Basic ".length());
        String[] values = new String(Base64.getDecoder().decode(base64Credentials)).split(":");
        return values[0];
    }
}
