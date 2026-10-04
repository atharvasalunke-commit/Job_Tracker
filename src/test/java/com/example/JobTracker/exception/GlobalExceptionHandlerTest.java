package com.example.JobTracker.exception;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    @Test
    void resourceNotFoundShouldReturn404() {
        ResponseEntity<String> response=handler.resourceNotFound(new ResourceNotFound("missing"));
        assertEquals(404, response.getStatusCode().value());
        assertEquals("missing", response.getBody());
    }

    @Test
    void resourceAlreadyExistsShouldReturn409() {
        ResponseEntity<String> response=handler.resourceAlreadyExists(new ResourceAlreadyExists("exists"));
        assertEquals(409, response.getStatusCode().value());
        assertEquals("exists", response.getBody());
    }

    @Test
    void invalidUrlShouldReturn400() {
        ResponseEntity<String> response=handler.invalidUrl(new InvalidUrlException("bad url"));
        assertEquals(400, response.getStatusCode().value());
        assertEquals("bad url", response.getBody());
    }

    @Test
    void pythonScraperExceptionShouldReturn503() {
        ResponseEntity<String> response=handler.pythonScraperException(new PythonScraperException("down"));
        assertEquals(503, response.getStatusCode().value());
        assertEquals("down", response.getBody());
    }

    @Test
    void genericExceptionShouldReturn500() {
        ResponseEntity<String> response= handler.handleGenericException(new RuntimeException("boom"));
        assertEquals(500, response.getStatusCode().value());
        assertEquals("An unexpected error occurred. Please try again.", response.getBody());
    }

    @Test
    void dataIntegrityViolationShouldReturn409() {
        ResponseEntity<String> response=handler.handleDataIntegrityViolation(new DataIntegrityViolationException("duplicate"));
        assertEquals(409, response.getStatusCode().value());
        assertEquals("Database conflict: This record already exists or violates constraints.", response.getBody());
    }

    @Test
    void invalidFieldsShouldReturn400WithFieldMessages() {
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException exception=mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(new FieldError("request", "username", "username required"), new FieldError("request", "email", "invalid email")));
        ResponseEntity<Map<String, String>> response=handler.handleInvalidFields(exception);
        assertEquals(400, response.getStatusCode().value());
        assertEquals("username required", response.getBody().get("username"));
        assertEquals("invalid email", response.getBody().get("email"));
    }
}
