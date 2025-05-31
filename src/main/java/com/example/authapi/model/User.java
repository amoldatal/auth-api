package com.example.authapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "comment")
    private String comment;

    // No-arg constructor
    public User() {
    }

    // All-arg constructor
    public User(String userId, String password, String nickname, String comment) {
        this.userId = userId;
        this.password = password;
        this.nickname = nickname;
        this.comment = comment;
    }

    // Getters and setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    // Optional: Builder-style static class
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String userId;
        private String password;
        private String nickname;
        private String comment;

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public User build() {
            return new User(userId, password, nickname, comment);
        }
    }
}
