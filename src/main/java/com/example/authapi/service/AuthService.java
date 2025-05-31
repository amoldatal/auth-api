package com.example.authapi.service;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.authapi.dto.SignupRequest;
import com.example.authapi.dto.SignupResponse;
import com.example.authapi.dto.UpdateRequest;
import com.example.authapi.dto.UserResponse;
import com.example.authapi.model.User;
import com.example.authapi.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public SignupResponse signup(SignupRequest request) {
        if (userRepository.findByUserId(request.getUser_id()).isPresent()) {
            throw new RuntimeException("Already same user_id is used");
        }

        User user = User.builder()
                .userId(request.getUser_id())
                .password(request.getPassword())
                .nickname(request.getUser_id()) // default nickname = user_id
                .comment("")
                .build();

        userRepository.save(user);

        return new SignupResponse("Account successfully created",
        	    new SignupResponse.User(user.getUserId(), user.getNickname(), user.getComment()));
    }

    public UserResponse getUser(String userId, String authHeader) {
        User authUser = getUserFromAuth(authHeader);
        User targetUser = userRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserResponse("User details by user_id",
            new UserResponse.User(targetUser.getUserId(),
                                  targetUser.getNickname(),
                                  targetUser.getComment()));
    }

    public UserResponse updateUser(String userId, String authHeader, UpdateRequest request) {
        User user = validateAuth(userId, authHeader);

        boolean updated = false;

        if (request.getNickname() != null) {
            user.setNickname(request.getNickname().isEmpty() ? user.getUserId() : request.getNickname());
            updated = true;
        }

        if (request.getComment() != null) {
            user.setComment(request.getComment().isEmpty() ? "" : request.getComment());
            updated = true;
        }

        if (!updated) {
            throw new IllegalArgumentException("required nickname or comment");
        }

        userRepository.save(user);

        return new UserResponse("User successfully updated",
                new UserResponse.User(user.getUserId(), user.getNickname(), user.getComment()));
    }

    public String deleteUser(String authHeader) {
        User user = getUserFromAuth(authHeader);
        userRepository.delete(user);
        return "Account and user successfully removed";
    }

    private User validateAuth(String userId, String authHeader) {
        User user = getUserFromAuth(authHeader);
        if (!user.getUserId().equals(userId)) {
            throw new SecurityException("No Permission for Update");
        }
        return user;
    }

    private User getUserFromAuth(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            throw new SecurityException("Authentication Failed");
        }

        try {
            String base64Credentials = authHeader.substring(6);
            String decoded = new String(Base64.getDecoder().decode(base64Credentials));
            String[] parts = decoded.split(":", 2);

            if (parts.length < 2) throw new SecurityException("Authentication Failed");

            String userId = parts[0];
            String password = parts[1];

            return userRepository.findByUserId(userId)
                .filter(user -> user.getPassword().equals(password))
                .orElseThrow(() -> new SecurityException("Authentication Failed"));
        } catch (Exception e) {
            throw new SecurityException("Authentication Failed");
        }
    }
}
