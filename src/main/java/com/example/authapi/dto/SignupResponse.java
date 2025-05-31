package com.example.authapi.dto;

public class SignupResponse {
    private String message;
    private User user;

    public SignupResponse() {}

    public SignupResponse(String message, User user) {
        this.message = message;
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static class User {
        private String user_id;
        private String nickname;
        private String comment; // ✅ required for test

        public User() {}

        public User(String user_id, String nickname, String comment) {
            this.user_id = user_id;
            this.nickname = nickname;
            this.comment = comment;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
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
    }
}
