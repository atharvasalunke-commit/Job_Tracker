package com.example.JobTracker.strategy;

import com.example.JobTracker.urlbuilder.JobSiteUrlBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor

public class InternshalaStrategy implements JobSiteStrategy {
    private final JobSiteUrlBuilder urlBuilder;

    @Override
    public String getDomain() {
        return "internshala";
    }

    @Override
    public String buildSearchUrl(List<String> userSkills) {
        return urlBuilder.buildUrl(getDomain(), userSkills);
    }
}