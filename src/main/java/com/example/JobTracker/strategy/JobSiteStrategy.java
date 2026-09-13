package com.example.JobTracker.strategy;

import java.util.List;

public interface JobSiteStrategy {
    String getDomain();
    String buildSearchUrl(List<String> userSkills);
}