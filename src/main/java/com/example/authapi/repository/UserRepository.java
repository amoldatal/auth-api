package com.example.authapi.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.authapi.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
}
