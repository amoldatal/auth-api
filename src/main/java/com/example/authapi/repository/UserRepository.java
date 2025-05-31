package com.example.authapi.repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.example.authapi.model.User;

@Repository
public class UserRepository {
    private static final ConcurrentHashMap<String, User> DB = new ConcurrentHashMap<>();

    public Optional<User> findById(String userId) {
        return Optional.ofNullable(DB.get(userId));
    }

    public void save(User user) {
        DB.put(user.getUserId(), user);
    }

    public void delete(String userId) {
        DB.remove(userId);
    }
}
