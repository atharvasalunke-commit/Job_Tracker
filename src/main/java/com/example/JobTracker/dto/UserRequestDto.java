package com.example.JobTracker.dto;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Data
public class UserRequestDto {
    @NotEmpty(message="it should contain characters between 3 and 100")
    private String domain_name;
    private List<String> user_skills;
    @NotEmpty
    private String job_type;
}
