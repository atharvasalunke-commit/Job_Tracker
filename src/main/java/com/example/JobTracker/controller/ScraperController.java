package com.example.JobTracker.controller;

import com.example.JobTracker.dto.JobApplicationResponseDto;
import com.example.JobTracker.dto.UserRequestDto;
import com.example.JobTracker.entity.ScraperConfig;
import com.example.JobTracker.service.JobScrapingService;
import com.example.JobTracker.service.ScraperConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class ScraperController {
    private final ScraperConfigService scraperConfigService;
    private final JobScrapingService jobScrapingService;

    @PostMapping("/dom_patterns")
    public ResponseEntity<ScraperConfig> configureDoms(@Valid @RequestBody ScraperConfig body) {
        ScraperConfig response = scraperConfigService.configure(body);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/scrape")
    public ResponseEntity<List<JobApplicationResponseDto>> triggerScrapeJob(@Valid @RequestBody UserRequestDto request) throws Exception {
        List<JobApplicationResponseDto> response = jobScrapingService.scrapeAndSaveJobs(request);
        return ResponseEntity.ok(response);
    }
}
