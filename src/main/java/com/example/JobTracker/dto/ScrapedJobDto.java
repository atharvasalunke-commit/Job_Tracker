package com.example.JobTracker.dto;

public class ScrapedJobDto {
    private String company_name;
    private String job_title;
    private String job_description;
    private String application_url;

    public String getCompany_name() { return company_name; }
    public void setCompany_name(String company_name) { this.company_name = company_name; }

    public String getJob_title() { return job_title; }
    public void setJob_title(String job_title) { this.job_title = job_title; }

    public String getJob_description() { return job_description; }
    public void setJob_description(String job_description) { this.job_description = job_description; }

    public String getApplication_url() { return application_url; }
    public void setApplication_url(String application_url) { this.application_url = application_url; }
}

