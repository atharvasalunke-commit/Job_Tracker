package com.example.JobTracker.dto;

public class JobApplicationRequestDto {
    private String company_name;
    private  String job_title;
    private String  status;
    private String application_url;

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getJob_title() {
        return job_title;
    }

    public void setJob_title(String job_title) {
        this.job_title = job_title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApplication_url() {
        return application_url;
    }

    public void setApplication_url(String application_url) {
        this.application_url = application_url;
    }
}
