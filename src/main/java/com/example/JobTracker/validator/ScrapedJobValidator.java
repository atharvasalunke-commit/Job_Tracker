package com.example.JobTracker.validator;

import com.example.JobTracker.dto.ScrapedJobDto;
import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
@org.springframework.stereotype.Component


public class ScrapedJobValidator {
    private final Pattern Url_Pattern = Pattern.compile("^(http|https)://[^\\s]+$");

    public boolean isValid(ScrapedJobDto scrapedJob, java.util.List<String> application_urls, java.util.Map<String, String> map2) {
        if (scrapedJob.getApplication_url() == null || !Url_Pattern.matcher(scrapedJob.getApplication_url()).matches()) {
            map2.put("Invalid Url", scrapedJob.getApplication_url());
            return false;
        }
        if (scrapedJob.getJob_title() == null || scrapedJob.getJob_title().length() > 100) {
            application_urls.add(scrapedJob.getApplication_url());
            return false;
        }
        if (scrapedJob.getCompany_name() == null || scrapedJob.getCompany_name().isEmpty()) {
            application_urls.add(scrapedJob.getApplication_url());
            return false;
        }
        return true;
    }
}
