package com.example.JobTracker.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class CreateJobApplicationRequestDto {
    @NotBlank
    @Size(min=3,max=100,message="company_name should have at least 3 and 100")
    private String company_name;
    @NotBlank
    @Size(min=3,max=100,message="job_title should have at least 3 and 100")
    private  String job_title;
    @NotBlank
    @Size(min=3,max=100,message="status should have at least 3 and 100")
    private String  status;
    @NotBlank
    @Size(min=3,max=100,message="application_url should have at least 3 and 100")
    private String application_url;
    @Size(min=3,max=100,message="job_description should have at least 3 characters")
    @NotBlank
    private String job_description;
}
