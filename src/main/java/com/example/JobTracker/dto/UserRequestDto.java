package com.example.JobTracker.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class UserRequestDto {
    @NotEmpty(message="it should contain characters between 3 and 100")
    private String domain_name;
    private List<String> user_skills;
    private String job_type;

    public String getDomain_name() {
        return domain_name;
    }

    public void setDomain_name(String domain_name) {
        this.domain_name = domain_name;
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
