package com.example.JobTracker.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import org.springframework.security.core.AuthenticationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<String> resourceNotFound(ResourceNotFound ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }
    @ExceptionHandler(TryAgainException.class)
    public ResponseEntity<String> tryAgain(ResourceNotFound ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }
    @ExceptionHandler(ResourceAlreadyExists.class)
    public ResponseEntity<String> resourceAlreadyExists(ResourceAlreadyExists ex) {
        return ResponseEntity.status(409).body(ex.getMessage());
    }
    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<String> invalidUrl(InvalidUrlException ex) {
        return ResponseEntity.status(400).body(ex.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> handleNoResourceFound(NoResourceFoundException ex) {
        return ResponseEntity.status(404).body("Not found");
    }

    @ExceptionHandler(PythonScraperException.class)
    public ResponseEntity<String> pythonScraperException(PythonScraperException ex) {
        return ResponseEntity.status(503).body(ex.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> authenticationException(AuthenticationException ex) {
        return ResponseEntity
                .status(401)
                .body("Invalid username or password");
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleInvalidFields(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(400).body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(500)
                .body("An unexpected error occurred. Please try again.");
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return ResponseEntity.status(409).body(
                "Database conflict: This record already exists or violates constraints."
        );
    }
}