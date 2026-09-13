package com.example.JobTracker.exception;


public class ScraperConfigException extends ScraperException {
    public ScraperConfigException(String message) {
        super(message);
    }

    public ScraperConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}