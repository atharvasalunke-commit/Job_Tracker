package com.example.JobTracker.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class JobApplicationRequestDto {
    private String company_name;
    private  String job_title;
    private String  status;
    private String application_url;
}
