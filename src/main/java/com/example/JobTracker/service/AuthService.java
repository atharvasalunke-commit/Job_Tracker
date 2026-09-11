package com.example.JobTracker.service;

import com.example.JobTracker.dto.AuthResponse;
import com.example.JobTracker.dto.AccountRequest;
import com.example.JobTracker.entity.User;
import com.example.JobTracker.globalexception.ResourceAlreadyExists;
import com.example.JobTracker.globalexception.ResourceNotFound;
import com.example.JobTracker.repository.Repository1;
import com.example.JobTracker.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private Repository1 repo;
    @Autowired
   private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordencoder;
    @Autowired
    private AuthenticationManager authenticationManager;
   public AuthResponse register(AccountRequest Request) {
       Optional<User> temp=repo.findByUsername(Request.getUsername());
       if(temp.isPresent()){
           throw(new ResourceAlreadyExists("Username already exists"));
       }
       User user = new User();
       user.setUsername(Request.getUsername());
       user.setEmail(Request.getEmail());
       user.setPassword_Hash(passwordencoder.encode(Request.getPassword()));
       user.setRole("USER");
       user.setCreated_at();
       repo.save(user);
       String jwt=jwtService.generateToken(user);
       return new AuthResponse(jwt);
   }
    public AuthResponse authenticate(AccountRequest Request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(Request.getUsername(),Request.getPassword()));
        User user=repo.findByUsername(Request.getUsername()).orElseThrow(()->new ResourceNotFound("User not found with this username"));
        String jwt=jwtService.generateToken(user);
        return new AuthResponse(jwt);
   }
}
