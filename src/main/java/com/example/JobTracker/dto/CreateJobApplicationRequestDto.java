package com.example.JobTracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateJobApplicationRequestDto {
    @NotBlank
    @Size(min=3,max=100,message="company_name should have atleast number of characters between 3 and 100")
    private String company_name;
    @NotBlank
    @Size(min=3,max=100,message="job_title should have atleast number of characters between 3 and 100")
    private  String job_title;
    @NotBlank
    @Size(min=3,max=100,message="status should have atleast number of characters between 3 and 100")
    private String  status;
    @NotBlank
    @Size(min=3,max=100,message="application_url should have atleast number of characters between 3 and 100")
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
