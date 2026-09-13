package com.example.JobTracker.dto;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class PythonScraperRequest {
  @NotEmpty
  private String url;
  @NotEmpty
  private ScraperConfigsDto rules;
  private List<String> user_skills;
  private String job_type;
}
