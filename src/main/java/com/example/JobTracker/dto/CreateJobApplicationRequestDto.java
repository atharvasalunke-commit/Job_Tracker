package com.example.JobTracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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
    public String getJob_description() {
        return job_description;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public void setJob_title(String job_title) {
        this.job_title = job_title;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setApplication_url(String application_url) {
        this.application_url = application_url;
    }

    public void setJob_description(String job_description) {
        this.job_description = job_description;
    }
}
