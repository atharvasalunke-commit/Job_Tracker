package com.example.JobTracker.service;

import com.example.JobTracker.entity.Role;
import com.example.JobTracker.entity.User;
import com.example.JobTracker.exception.ResourceNotFound;
import com.example.JobTracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailServiceTest {

    @Test
    void loadUserByUsernameShouldReturnUserDetails() {
        UserRepository repo = mock(UserRepository.class);
        CustomUserDetailService service = new CustomUserDetailService(repo);
        User user = new User();
        user.setUsername("Atharva");
        user.setPassword_Hash("hashed");
        user.setRole(Role.USER);
        when(repo.findByUsername("Atharva")).thenReturn(Optional.of(user));
        UserDetails result = service.loadUserByUsername("Atharva");
        assertEquals("Atharva", result.getUsername());
        assertEquals("hashed", result.getPassword());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsernameShouldThrowWhenUserDoesNotExist() {
        UserRepository repo = mock(UserRepository.class);
        CustomUserDetailService service = new CustomUserDetailService(repo);
        when(repo.findByUsername("Missing")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFound.class, () -> service.loadUserByUsername("Missing"));
    }
}
