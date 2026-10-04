package com.example.JobTracker.security;

import com.example.JobTracker.entity.Role;
import com.example.JobTracker.entity.User;
import com.example.JobTracker.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldContinueWhenAuthorizationHeaderMissing() throws Exception {
        JwtService jwtService = mock(JwtService.class);
        UserRepository repo = mock(UserRepository.class);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, repo);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        when(request.getHeader("Authorization")).thenReturn(null);
        filter.doFilterInternal(request, response, chain);
        verify(chain).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldAuthenticateUserFromValidJwt() throws Exception {
        JwtService jwtService = mock(JwtService.class);
        UserRepository repo = mock(UserRepository.class);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, repo);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        User user = new User();
        user.setUsername("Atharva");
        user.setRole(Role.USER);
        when(request.getHeader("Authorization")).thenReturn("Bearer abc");
        when(jwtService.extractUsername("abc")).thenReturn("Atharva");
        when(repo.findByUsername("Atharva")).thenReturn(Optional.of(user));
        filter.doFilterInternal(request, response, chain);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertSame(user,SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        assertTrue(SecurityContextHolder.getContext().getAuthentication()
                        .getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        verify(chain).doFilter(request, response);
    }

    @Test
    void shouldClearContextForInvalidJwt() throws Exception {
        JwtService jwtService = mock(JwtService.class);
        UserRepository repo = mock(UserRepository.class);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, repo);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer bad");
        when(jwtService.extractUsername("bad")).thenThrow(new JwtException("invalid"));
        filter.doFilterInternal(request, response, chain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }
}
