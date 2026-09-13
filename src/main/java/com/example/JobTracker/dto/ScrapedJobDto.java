package com.example.JobTracker.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ScrapedJobDto {
    private String company_name;
    private String job_title;
    private String job_description;
    @NotEmpty
    private String application_url;
}

