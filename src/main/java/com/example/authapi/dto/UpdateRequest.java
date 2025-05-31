package com.example.authapi.dto;

public class UpdateRequest {
    private String nickname;
    private String comment;

    public UpdateRequest() {}

    public UpdateRequest(String nickname, String comment) {
        this.nickname = nickname;
        this.comment = comment;
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
