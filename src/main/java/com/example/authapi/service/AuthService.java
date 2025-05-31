package com.example.authapi.service;

import com.example.authapi.model.User;
import com.example.authapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository repo;

    public User signup(String userId, String password) {
        validateSignup(userId, password);
        if (repo.existsById(userId)) {
            throw new RuntimeException("Account creation failed");
        }
        User user = new User(userId, password);
        return repo.save(user);
    }

    public User getUser(String authHeader, String userId) {
        User authUser = authorize(authHeader);
        if (!authUser.getUserId().equals(userId)) {
            throw new RuntimeException("No Permission for Update");
        }
        return authUser;
    }

    public User updateUser(String authHeader, String userId, String nickname, String comment) {
        if (nickname == null && comment == null) {
            throw new RuntimeException("User updation failed: required nickname or comment");
        }
        User user = getUser(authHeader, userId);
        if (nickname != null) user.setNickname(nickname.isEmpty() ? userId : nickname);
        if (comment != null) user.setComment(comment);
        return repo.save(user);
    }

    public void deleteUser(String authHeader, String userId) {
        getUser(authHeader, userId);
        repo.deleteById(userId);
    }

    private User authorize(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            throw new RuntimeException("Authentication Failed");
        }
        String base64Credentials = authHeader.substring("Basic ".length());
        String[] values = new String(Base64.getDecoder().decode(base64Credentials)).split(":");

        if (values.length != 2) {
            throw new RuntimeException("Authentication Failed");
        }

        String userId = values[0];
        String password = values[1];

        Optional<User> userOpt = repo.findById(userId);
        if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(password)) {
            throw new RuntimeException("Authentication Failed");
        }

        return userOpt.get();
    }

    private void validateSignup(String userId, String password) {
        if (userId == null || userId.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            throw new RuntimeException("Missing required fields: user_id and password");
        }
    }
}
