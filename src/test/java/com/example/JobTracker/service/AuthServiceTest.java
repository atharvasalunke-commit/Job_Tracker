package com.example.JobTracker.service;

import com.example.JobTracker.dto.AccountRequest;
import com.example.JobTracker.entity.User;
import com.example.JobTracker.exception.ResourceAlreadyExists;
import com.example.JobTracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository repo;
    @InjectMocks

    private AuthService authService;

    @Test
    void checkIfUsernameTakenThrowsException() {
        AccountRequest request = new AccountRequest();
        request.setUsername("john");
        request.setPassword("password123");
        request.setEmail("john@gmail.com");
        
        when(repo.findByUsername("john")).thenReturn(Optional.of(new User()));

        assertThrows(ResourceAlreadyExists.class, () -> authService.register(request));
        verify(repo, never()).save(any());
    }

    @Test
    void checkIfEmailTakenThrowsException() {
        AccountRequest request = new AccountRequest();
        request.setUsername("john");
        request.setPassword("password123");
        request.setEmail("john@gmail.com");
        
        when(repo.findByUsername("john")).thenReturn(Optional.empty());
        when(repo.findByEmail("john@gmail.com")).thenReturn(Optional.of(new User()));

        assertThrows(ResourceAlreadyExists.class, () -> authService.register(request));
        verify(repo, never()).save(any());
    }
}
