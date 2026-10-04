package com.example.JobTracker.service;

import com.example.JobTracker.client.PythonScraperClient;
import com.example.JobTracker.dto.JobApplicationResponseDto;
import com.example.JobTracker.dto.ScrapedJobDto;
import com.example.JobTracker.dto.UserRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobScrapingServiceTest {

    @Mock
    private PythonScraperClient pythonScraperClient;

    @Mock
    private JobApplicationService jobApplicationService;

    @InjectMocks
    private JobScrapingService service;

    @Test
    void scrapeAndSaveJobsShouldReturnSavedJobs() throws Exception {
        UserRequestDto request = new UserRequestDto();
        ScrapedJobDto job = new ScrapedJobDto();
        job.setCompany_name("Google");
        job.setJob_title("Java Developer");
        JobApplicationResponseDto response= new JobApplicationResponseDto();
        when(pythonScraperClient.scrape(any())).thenReturn(List.of(job));
        when(jobApplicationService.createScrapedJob(job)).thenReturn(response);
        List<JobApplicationResponseDto> result= service.scrapeAndSaveJobs(request);
        assertEquals(1, result.size());
        assertSame(response, result.get(0));
    }

    @Test
    void scrapeAndSaveJobsShouldReturnEmptyList() throws Exception {
        UserRequestDto request = new UserRequestDto();
        when(pythonScraperClient.scrape(any())).thenReturn(List.of());
        List<JobApplicationResponseDto> result = service.scrapeAndSaveJobs(request);
        assertTrue(result.isEmpty());
    }
}