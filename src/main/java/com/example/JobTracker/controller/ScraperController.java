package com.example.JobTracker.controller;

import com.example.JobTracker.dto.PythonScraperRequest;
import com.example.JobTracker.dto.JobApplicationResponseDto;
import com.example.JobTracker.dto.ScrapedJobDto;
import com.example.JobTracker.dto.UserRequestDto;
import com.example.JobTracker.entity.ScraperConfig;
import com.example.JobTracker.service.JobApplicationService;
import com.example.JobTracker.service.ScraperService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class ScraperController {
    private Map<String,String>Job_Sites=Map.ofEntries(
            Map.entry("authenticjobs", "https://authenticjobs.com/?s=%s"),
            Map.entry("internshala","https://internshala.com/internships/keywords-%s/")
    );
    @Autowired
    private ScraperService service;
    @Autowired
    private JobApplicationService service2;

    @PostMapping("/dom_patterns")
    public ResponseEntity<ScraperConfig> configureDoms(@Valid @RequestBody ScraperConfig body) {
        ScraperConfig response = service.configure(body);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/scrape")
    public ResponseEntity<List<JobApplicationResponseDto>> triggerScrapeJob(@Valid @RequestBody UserRequestDto request) {
        PythonScraperRequest final_request=service.requestCompleter(request,Job_Sites);
        List<ScrapedJobDto> response = service.triggerScraper(final_request,Job_Sites);
        List<JobApplicationResponseDto>final_response=new ArrayList<>();
        for(int i=0;i<response.size();i++) {
            ScrapedJobDto temp = response.get(i);
            JobApplicationResponseDto temp_response = service2.createScrapedJob(temp);
            final_response.add(temp_response);
        }
        if(!final_response.isEmpty()) {
            return ResponseEntity.ok(final_response);
        }
        return ResponseEntity.status(200).build();
    }
}
