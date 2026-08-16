package com.example.JobTracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class JobApplicationRequestDto {

private String company_name;
    private  String job_title;
    private String  status;
    private String application_url;
    public String getCompany_name() {
        return company_name;
    }

    public String getJob_title() {
        return job_title;
    }

    public String getStatus() {
        return status;
    }

    public String getApplication_url() {
        return application_url;
    }
}
