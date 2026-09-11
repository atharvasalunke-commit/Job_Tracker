package com.example.JobTracker.controller;

import com.example.JobTracker.dto.AccountRequest;
import com.example.JobTracker.dto.AuthResponse;
import com.example.JobTracker.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
public class AuthController {
    private AuthService Service;
    AuthController(AuthService Service){
        this.Service=Service;
    }
    @PostMapping("/Register")
    public ResponseEntity<AuthResponse>Register(@Valid @RequestBody AccountRequest request){
        AuthResponse auth= Service.register(request);
        return ResponseEntity.ok(auth);
    }
    @PostMapping("/Login")
    public ResponseEntity<AuthResponse>Login(@Valid @RequestBody AccountRequest request){
        AuthResponse auth= Service.authenticate(request);
        return ResponseEntity.ok(auth);
    }
}
