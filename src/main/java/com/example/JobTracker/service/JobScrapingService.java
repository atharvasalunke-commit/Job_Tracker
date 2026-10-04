package com.example.JobTracker.service;

import com.example.JobTracker.client.PythonScraperClient;
import com.example.JobTracker.dto.JobApplicationResponseDto;
import com.example.JobTracker.dto.PythonScraperRequest;
import com.example.JobTracker.dto.ScrapedJobDto;
import com.example.JobTracker.dto.UserRequestDto;
import com.example.JobTracker.exception.PythonScraperException;
import com.example.JobTracker.exception.ResourceAlreadyExists;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobScrapingService {

    private static final Logger log =
            LoggerFactory.getLogger(JobScrapingService.class);

    private static final int MAX_JOBS_TO_SAVE_PER_SCRAPE = 50;

    private final PythonScraperClient pythonScraperClient;
    private final JobApplicationService jobApplicationService;

    public List<JobApplicationResponseDto> scrapeAndSaveJobs(UserRequestDto request) throws PythonScraperException {
        PythonScraperRequest scraperRequest=new PythonScraperRequest();
        scraperRequest.setSite_name(request.getSite_name());
        scraperRequest.setSearch_term(request.getSearch_term());
        scraperRequest.setLocation(request.getLocation());
        scraperRequest.setUser_skills(request.getUser_skills());
        scraperRequest.setJob_type(request.getJob_type());
        scraperRequest.setIs_remote(request.getIs_remote());
        List<ScrapedJobDto> scrapedJobs=pythonScraperClient.scrape(scraperRequest);
        List<JobApplicationResponseDto> savedJobs=new ArrayList<>();
        if (scrapedJobs == null || scrapedJobs.isEmpty()) {
            log.info("Python scraper returned 0 jobs.");
            return savedJobs;
        }
        log.info("Python scraper returned {} jobs.", scrapedJobs.size());
        int savedCount = 0;
        int duplicateCount = 0;
        int failedCount = 0;
        for (ScrapedJobDto scrapedJob : scrapedJobs) {
            if (savedCount >= MAX_JOBS_TO_SAVE_PER_SCRAPE) {
                log.info("Reached maximum of {} saved jobs.", MAX_JOBS_TO_SAVE_PER_SCRAPE);
                break;
            }
            try {
                JobApplicationResponseDto saved= jobApplicationService.createScrapedJob(scrapedJob);
                if (saved != null) {
                    savedJobs.add(saved);
                    savedCount++;
                    log.info("SAVED: {} | {}", scrapedJob.getCompany_name(), scrapedJob.getJob_title());
                }
            } catch (ResourceAlreadyExists e) {
                duplicateCount++;
                log.info("DUPLICATE: {} | URL: {}", scrapedJob.getJob_title(), scrapedJob.getApplication_url());
            } catch (Exception e) {
                failedCount++;
                log.error("FAILED TO SAVE: {} | company={} | url={}", scrapedJob.getJob_title(), scrapedJob.getCompany_name(), scrapedJob.getApplication_url(), e);
            }
        }
        log.info("SCRAPE COMPLETE | received={} | saved={} | duplicates={} | failed={}", scrapedJobs.size(), savedCount, duplicateCount, failedCount);
        return savedJobs;
    }
}
