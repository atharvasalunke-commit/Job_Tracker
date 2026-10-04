package com.example.JobTracker.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class PythonScraperRequest {
  private List<String> site_name;
  private String search_term;
  private String location;
  private List<String> user_skills;
  private String job_type;
  private Boolean is_remote;
}