package com.example.JobTracker.controller;

import com.example.JobTracker.dto.*;
import com.example.JobTracker.service.JobScrapingService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScraperControllerTest {

    @Test
    void triggerScrapeJobShouldReturnSavedJobs() throws Exception {
        JobScrapingService service = mock(JobScrapingService.class);
        ScraperController controller = new ScraperController(service);
        UserRequestDto request = new UserRequestDto();
        List<JobApplicationResponseDto> expected = List.of(new JobApplicationResponseDto());
        when(service.scrapeAndSaveJobs(request)).thenReturn(expected);
        ResponseEntity<List<JobApplicationResponseDto>> response=controller.triggerScrapeJob(request);
        assertEquals(200, response.getStatusCode().value());
        assertSame(expected, response.getBody());
        verify(service).scrapeAndSaveJobs(request);
    }
}
