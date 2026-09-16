package com.example.JobTracker.service;

import com.example.JobTracker.client.PythonScraperClient;
import com.example.JobTracker.dto.JobApplicationResponseDto;
import com.example.JobTracker.dto.PythonScraperRequest;
import com.example.JobTracker.dto.ScrapedJobDto;
import com.example.JobTracker.dto.UserRequestDto;
import com.example.JobTracker.exception.InvalidUrlException;
import com.example.JobTracker.exception.ScraperConfigException;
import com.example.JobTracker.validator.ScrapedJobValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor

public class JobScrapingService {
    private final ScraperConfigService scraperConfigService;
    private final PythonScraperClient pythonScraperClient;
    private final JobApplicationService jobApplicationService;
    private final ScrapedJobValidator validator;

    public List<JobApplicationResponseDto> scrapeAndSaveJobs(UserRequestDto request) throws Exception {
        PythonScraperRequest scraperRequest = scraperConfigService.prepareRequest(request);
        List<ScrapedJobDto> scrapedJobs = pythonScraperClient.scrape(scraperRequest);
        ValidationResult validation = validate(scrapedJobs);
        if (!validation.urlsNeedingReconfiguration().isEmpty()) {
            scraperConfigService.regenerateConfig(request.getDomain_name(), scraperRequest.getUrl());
            scraperRequest = scraperConfigService.prepareRequest(request);
            validation = validate(pythonScraperClient.scrape(scraperRequest));
            if (!validation.urlsNeedingReconfiguration().isEmpty()) {
                throw new ScraperConfigException("Could not extract valid job titles and companies after refreshing the scraper configuration");
            }
        }

        if (!validation.invalidUrls().isEmpty()) {
            StringBuilder message = new StringBuilder();
            for (Map.Entry<String, String> entry : validation.invalidUrls().entrySet()) {
                message.append(entry.getKey()).append(": ")
                        .append(entry.getValue()).append("\n");
            }
            throw new InvalidUrlException(message.toString());
        }

        List<JobApplicationResponseDto> savedJobs = new ArrayList<>();
        Set<String> seenJobs = new HashSet<>();
        
        for (ScrapedJobDto scrapedJob : validation.validJobs()) {
            
            String uniqueKey = scrapedJob.getApplication_url();
            boolean alreadySeenInThisRun = !seenJobs.add(uniqueKey);

            if (alreadySeenInThisRun) {
                System.out.println("SKIPPED (duplicate in this scrape): " + scrapedJob.getJob_title() + " | " + scrapedJob.getApplication_url());
                continue;
            }

            try {
                JobApplicationResponseDto saved = jobApplicationService.createScrapedJob(scrapedJob);
                if (saved != null) {
                    savedJobs.add(saved);
                }
            } catch (com.example.JobTracker.exception.ResourceAlreadyExists e) {
                System.out.println("SKIPPED (already in database): " + scrapedJob.getJob_title() + " | " + scrapedJob.getApplication_url());
            } catch (Exception e) {
                System.out.println("FAILED to save: " + scrapedJob.getJob_title() + " | reason: " + e.getMessage());
            }
        }
        return savedJobs;
    }

    private ValidationResult validate(List<ScrapedJobDto> scrapedJobs) {
        List<String> urlsNeedingReconfiguration = new ArrayList<>();
        Map<String, String> invalidUrls = new HashMap<>();
        List<ScrapedJobDto> validJobs = new ArrayList<>();
        for (ScrapedJobDto job : scrapedJobs) {
            if (validator.isValid(job, urlsNeedingReconfiguration, invalidUrls)) {
                validJobs.add(job);
            }
        }
        return new ValidationResult(validJobs, urlsNeedingReconfiguration, invalidUrls);
    }

    private record ValidationResult(
            List<ScrapedJobDto> validJobs,
            List<String> urlsNeedingReconfiguration,
            Map<String, String> invalidUrls
    ) {}
}
