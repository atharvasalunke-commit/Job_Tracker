package com.example.JobTracker.exception;


public class PythonScraperException extends ScraperException {
    public PythonScraperException(String message) {
        super(message);
    }

    public PythonScraperException(String message, Throwable cause) {
        super(message, cause);
    }
}