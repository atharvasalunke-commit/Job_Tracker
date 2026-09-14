package com.example.JobTracker.service;

import lombok.RequiredArgsConstructor;
import com.example.JobTracker.dto.AuthResponse;
import com.example.JobTracker.dto.AccountRequest;
import com.example.JobTracker.dto.LoginRequest;
import com.example.JobTracker.entity.User;
import com.example.JobTracker.entity.Role;
import com.example.JobTracker.exception.ResourceAlreadyExists;
import com.example.JobTracker.exception.ResourceNotFound;
import com.example.JobTracker.repository.UserRepository;
import com.example.JobTracker.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class AuthService {
    private final UserRepository repo;
   private final JwtService jwtService;
    private final PasswordEncoder passwordencoder;
    private final AuthenticationManager authenticationManager;

   public AuthResponse register(AccountRequest Request) {
       Optional<User> temp=repo.findByUsername(Request.getUsername());
       Optional<User> temp2 = repo.findByEmail(Request.getEmail());
       if(temp.isPresent()){
           throw(new ResourceAlreadyExists("Username already exists"));
       }
       if(temp2.isPresent()){
           throw(new ResourceAlreadyExists("Email already exists"));
       }
       User user = new User();
       user=prepareNewAccount(Request,user);
       repo.save(user);
       String jwt=jwtService.generateToken(user);
       return new AuthResponse(jwt, user.getRole().name());
   }

    public AuthResponse authenticate(LoginRequest Request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(Request.getUsername(),Request.getPassword()));
        User user=repo.findByUsername(Request.getUsername()).orElseThrow(()->new ResourceNotFound("User not found with this username"));
        String jwt=jwtService.generateToken(user);
        return new AuthResponse(jwt, user.getRole().name());
   }

    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    User prepareNewAccount(AccountRequest Request,User user){
        user.setUsername(Request.getUsername());
        user.setEmail(Request.getEmail());
        user.setPassword_Hash(passwordencoder.encode(Request.getPassword()));
        user.setRole(Role.USER);
        user.setCreated_at();
        return user;
    }
}
