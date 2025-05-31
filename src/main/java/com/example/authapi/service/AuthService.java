package com.example.authapi.service;

import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.authapi.model.User;
import com.example.authapi.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository repo;

    public User signup(String userId, String password) {
        validateSignup(userId, password);
        if (repo.findById(userId).isPresent()) {
            throw new RuntimeException("Account creation failed: already same user_id exists");
        }
        User user = new User(userId, password);
        repo.save(user);
        return user;
    }

    public User getUser(String authHeader, String userId) {
        User authUser = authorize(authHeader);
        if (!authUser.getUserId().equals(userId)) {
            throw new RuntimeException("403 - No Permission for this user");
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
        return user;
    }

    public void deleteUser(String authHeader, String userId) {
        getUser(authHeader, userId);
        repo.delete(userId);
    }

    private User authorize(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            throw new RuntimeException("401 - Authentication Failed");
        }
        String base64Credentials = authHeader.substring("Basic ".length());
        String[] values = new String(Base64.getDecoder().decode(base64Credentials)).split(":");
        String userId = values[0];
        String password = values[1];

        Optional<User> userOpt = repo.findById(userId);
        if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(password)) {
            throw new RuntimeException("401 - Authentication Failed");
        }
        return userOpt.get();
    }

    private void validateSignup(String userId, String password) {
        if (userId == null || password == null || userId.length() < 6 || userId.length() > 20 ||
            password.length() < 8 || password.length() > 20) {
            throw new RuntimeException("400 - Invalid signup details");
        }
    }
}
