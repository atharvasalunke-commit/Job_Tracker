package com.example.JobTracker.service;

import com.example.JobTracker.dto.AccountRequest;
import com.example.JobTracker.dto.LoginRequest;
import com.example.JobTracker.entity.User;
import com.example.JobTracker.exception.ResourceAlreadyExists;
import com.example.JobTracker.exception.ResourceNotFound;
import com.example.JobTracker.exception.TryAgainException;
import com.example.JobTracker.repository.UserRepository;
import com.example.JobTracker.security.JwtService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static com.example.JobTracker.entity.Role.USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private  UserRepository repo;
    @Mock
    private  JwtService jwtService;
    @Mock
    private  PasswordEncoder passwordencoder;
    @Mock
    private  AuthenticationManager authenticationManager;
    @InjectMocks
    private AuthService authService;
    @Test
    void registerCheckIfDuplicateUsernameIsNotSaved() {
        AccountRequest request=new AccountRequest();
        request.setUsername("Atharva");
        User user=new User();
        when(repo.findByUsername(request.getUsername())).thenReturn(Optional.of(user));
        assertThrows(ResourceAlreadyExists.class,()->authService.register(request));
        verify(repo,never()).save(any(User.class));
    }
    @Test
    void registerCheckIfDuplicateEmailIsNotSaved() {
        AccountRequest request=new AccountRequest();
        request.setEmail("Atharva@");
        User user=new User();
        when(repo.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        assertThrows(ResourceAlreadyExists.class,()->authService.register(request));
        verify(repo,never()).save(any(User.class));
    }
    @Test
    void registerCheckIfAccountIsSaved() {
        AccountRequest request=new AccountRequest();
        request.setUsername("Atharva");
        request.setEmail("Atharva@");
        when(repo.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(repo.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        authService.register(request);
        verify(repo).save(any(User.class));
    }
    @Test
    void checkIfAccountIsuthenticate() {
        LoginRequest request=new LoginRequest();
        request.setPassword("r2u59u2jof");
        request.setUsername("Atharva");
        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword()))).thenThrow(new ResourceNotFound("HI"));
        assertThrows(ResourceNotFound.class,()->authService.authenticate(request));
        verify(repo,never()).findByUsername(request.getUsername());
    }
    @Test
    void checkIfAccountGotJwt(){
        LoginRequest request=new LoginRequest();
        request.setPassword("r2u59u2jof");
        request.setUsername("Atharva");
        User user=new User();
        user.setPassword_Hash("r2u59u2jof");
        user.setUsername("Atharva");
        user.setRole(USER);
        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword()))).thenReturn(mock(Authentication.class));
        when(repo.findByUsername(request.getUsername())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("hnnneifiniqq");
        authService.authenticate(request);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
;       verify(repo).findByUsername(request.getUsername());
        verify(jwtService).generateToken(user);
    }
}