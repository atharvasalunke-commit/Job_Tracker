package com.example.JobTracker.security;

import com.example.JobTracker.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService service;

    @BeforeEach
    void setUp() throws Exception {

        service = new JwtService();

        String secret = Base64.getEncoder().encodeToString(
                "01234567890123456789012345678901"
                        .getBytes()
        );

        Field secretField =
                JwtService.class.getDeclaredField("secretKey");
        secretField.setAccessible(true);
        secretField.set(service, secret);

        Field expirationField =
                JwtService.class.getDeclaredField("jwtExpiration");
        expirationField.setAccessible(true);
        expirationField.set(service, 3600000L);
    }

    @Test
    void generateTokenShouldContainUsername() {

        User user = new User();
        user.setUsername("atharva");

        String token = service.generateToken(user);

        assertNotNull(token);
        assertEquals(
                "atharva",
                service.extractUsername(token)
        );
    }

    @Test
    void extractUsernameShouldReturnCorrectUsername() {

        User user = new User();
        user.setUsername("testuser");

        String token = service.generateToken(user);

        assertEquals(
                "testuser",
                service.extractUsername(token)
        );
    }

    @Test
    void invalidTokenShouldThrowException() {

        assertThrows(
                Exception.class,
                () -> service.extractUsername("invalid-token")
        );
    }
}