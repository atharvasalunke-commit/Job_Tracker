package com.example.JobTracker.controller;

import com.example.JobTracker.dto.JobApplicationResponseDto;
import com.example.JobTracker.dto.UserRequestDto;
import com.example.JobTracker.service.JobScrapingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ScraperController {
    private final JobScrapingService jobScrapingService;

    @PostMapping("/scrape")
    public ResponseEntity<List<JobApplicationResponseDto>> triggerScrapeJob(@Valid @RequestBody UserRequestDto request) throws Exception {
        List<JobApplicationResponseDto> response=jobScrapingService.scrapeAndSaveJobs(request);
        return ResponseEntity.ok(response);
    }
}
