package com.example.authapi.controller;

import java.util.HashMap;
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

import com.example.authapi.service.AuthService;

@RestController
public class UserController {

    @Autowired
    private AuthService authService;

    private ResponseEntity<Map<String, String>> error(int code, String msg) {
        Map<String, String> body = new HashMap<>();
        body.put("message", msg);
        return ResponseEntity.status(code).body(body);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> payload) {
        try {
            String userId = payload.get("user_id");
            String password = payload.get("password");
            if (userId == null || password == null) {
                return error(400, "Missing user_id or password");
            }
            return ResponseEntity.ok(authService.signup(userId, password));
        } catch (RuntimeException e) {
            return error(400, e.getMessage());
        }
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUser(@RequestHeader(value = "Authorization", required = false) String auth,
                                     @PathVariable String userId) {
        try {
            return ResponseEntity.ok(authService.getUser(auth, userId));
        } catch (RuntimeException e) {
            return error(getCodeFromMessage(e.getMessage()), e.getMessage().replaceAll("^\\d+ - ", ""));
        }
    }

    @PatchMapping("/users/{userId}")
    public ResponseEntity<?> updateUser(@RequestHeader(value = "Authorization", required = false) String auth,
                                        @PathVariable String userId,
                                        @RequestBody Map<String, String> payload) {
        try {
            String nickname = payload.get("nickname");
            String comment = payload.get("comment");
            return ResponseEntity.ok(authService.updateUser(auth, userId, nickname, comment));
        } catch (RuntimeException e) {
            return error(getCodeFromMessage(e.getMessage()), e.getMessage().replaceAll("^\\d+ - ", ""));
        }
    }

    @PostMapping("/close")
    public ResponseEntity<?> close(@RequestHeader(value = "Authorization", required = false) String auth,
                                   @RequestBody Map<String, String> payload) {
        try {
            String userId = payload.get("user_id");
            authService.deleteUser(auth, userId);
            return ResponseEntity.ok(Map.of("message", "Account deleted"));
        } catch (RuntimeException e) {
            return error(getCodeFromMessage(e.getMessage()), e.getMessage().replaceAll("^\\d+ - ", ""));
        }
    }

    private int getCodeFromMessage(String msg) {
        if (msg.matches("^\\d+ - .*")) {
            return Integer.parseInt(msg.split(" ", 2)[0]);
        }
        return 400;
    }
}
