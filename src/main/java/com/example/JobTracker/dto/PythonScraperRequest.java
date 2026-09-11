package com.example.JobTracker.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class PythonScraperRequest {
  @NotEmpty
  private String url;
  private ScraperConfigsDto rules;
  private List<String> user_skills;
  @NotEmpty
  private String job_type;

  public String getUrl() {
      return url;
  }

  public void setUrl(String url) {
      this.url = url;
  }

  public ScraperConfigsDto getRules() {
      return rules;
  }

  public void setRules(ScraperConfigsDto rules) {
      this.rules = rules;
  }

  public List<String> getUser_skills() {
      return user_skills;
  }

  public void setUser_skills(List<String> user_skills) {
      this.user_skills = user_skills;
  }

  public String getJob_type() {
      return job_type;
  }

  public void setJob_type(String job_type) {
      this.job_type = job_type;
  }
}
