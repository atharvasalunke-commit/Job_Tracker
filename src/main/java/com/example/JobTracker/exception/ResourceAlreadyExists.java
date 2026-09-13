package com.example.JobTracker.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.CONFLICT)

public class ResourceAlreadyExists extends RuntimeException{
    public ResourceAlreadyExists(String msg){
        super(msg);
    }
}
