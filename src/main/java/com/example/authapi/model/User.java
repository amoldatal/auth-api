package com.example.authapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;

@Entity
public class User {
	@JsonProperty("user_id")
    private String userId;

    private String password;
    private String nickname;
    private String comment;

    public User() {}

    public User(String userId, String password) {
        this.userId = userId;
        this.password = password;
        this.nickname = userId;
        this.comment = "";
    }

    public User(String userId, String password, String nickname, String comment) {
        this.userId = userId;
        this.password = password;
        this.nickname = nickname;
        this.comment = comment;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
