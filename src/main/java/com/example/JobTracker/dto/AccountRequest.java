package com.example.JobTracker.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class AccountRequest {
    @NotEmpty
    @Size(min=3,max=100,message="username should have atleast number of characters between 3 and 100")
    private String username;
    @NotEmpty
    @Size(min=3,max=100,message="password should have atleast number of characters between 3 and 100")
    private  String password;
    @NotEmpty
    @Size(min=3,max=100,message="email should have atleast number of characters between 3 and 100")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
