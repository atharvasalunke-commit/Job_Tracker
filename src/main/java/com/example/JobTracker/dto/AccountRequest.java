package com.example.JobTracker.dto;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Data
public class AccountRequest {
    @NotEmpty
    @Size(min=3,max=100,message="username should have at least 3 and 100")
    private String username;
    @NotEmpty
    @Size(min=3,max=100,message="password should have at least 3 and 100")
    private  String password;
    @NotEmpty
    @jakarta.validation.constraints.Email(message="Invalid email format")
    private String email;
    public AccountRequest(){}
    public AccountRequest(String atharva, int i, String mail) {
    }
}
