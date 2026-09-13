package com.example.JobTracker.dto;

import lombok.Data;

@Data
public class JobApplicationResponseDto {
    private Long id;
    private String company_name;
    private  String job_title;
    private String  status;
    private String application_url;
    private String created_at;
    private String updated_at;
    private String message;
    private String  job_description;
}
