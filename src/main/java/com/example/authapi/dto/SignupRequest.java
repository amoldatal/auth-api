package com.example.authapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SignupRequest {


    @NotBlank(message = "user_id is required")
    @Size(min = 6, max = 20, message = "user_id must be between 6 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "user_id must contain only alphanumeric characters")
    private String user_id;

    @NotBlank(message = "password is required")
    @Size(min = 8, max = 20, message = "password must be between 8 and 20 characters")
    @Pattern(regexp = "^[\\x21-\\x7E]+$", message = "password must contain only printable ASCII characters")
    private String password;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
