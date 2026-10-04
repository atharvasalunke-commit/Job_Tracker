package com.example.JobTracker.controller;

import com.example.JobTracker.dto.*;
import com.example.JobTracker.service.JobApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JobApplicationControllerTest {

    @Test
    void getByIdShouldReturnJob() {
        JobApplicationService service = mock(JobApplicationService.class);
        JobApplicationController controller = new JobApplicationController(service);
        JobApplicationResponseDto expected = new JobApplicationResponseDto();
        when(service.GetJobApplicationById(1L)).thenReturn(expected);
        ResponseEntity<JobApplicationResponseDto> response = controller.getById(1L);
        assertEquals(200, response.getStatusCode().value());
        assertSame(expected, response.getBody());
    }

    @Test
    void getAllShouldReturnJobs() {
        JobApplicationService service = mock(JobApplicationService.class);
        JobApplicationController controller = new JobApplicationController(service);
        List<JobApplicationResponseDto> expected = List.of(new JobApplicationResponseDto());
        when(service.GetJobApplications(2, 5)).thenReturn(expected);
        ResponseEntity<List<JobApplicationResponseDto>> response = controller.getAll(2, 5);
        assertEquals(200, response.getStatusCode().value());
        assertSame(expected, response.getBody());
    }

    @Test
    void createShouldReturn201() throws Exception {
        JobApplicationService service = mock(JobApplicationService.class);
        JobApplicationController controller = new JobApplicationController(service);
        CreateJobApplicationRequestDto request = new CreateJobApplicationRequestDto();
        JobApplicationResponseDto expected = new JobApplicationResponseDto();
        when(service.createJobApplication(request)).thenReturn(expected);
        ResponseEntity<JobApplicationResponseDto> response = controller.create(request);
        assertEquals(201, response.getStatusCode().value());
        assertSame(expected, response.getBody());
    }

    @Test
    void updateShouldReturn200() throws Exception {
        JobApplicationService service = mock(JobApplicationService.class);
        JobApplicationController controller = new JobApplicationController(service);
        JobApplicationRequestDto request = new JobApplicationRequestDto();
        JobApplicationResponseDto expected = new JobApplicationResponseDto();
        when(service.updateJobApplication(1L, request)).thenReturn(expected);
        ResponseEntity<JobApplicationResponseDto> response=controller.update(1L, request);
        assertEquals(200, response.getStatusCode().value());
        assertSame(expected, response.getBody());
    }

    @Test
    void softDeleteShouldReturn200() {
        JobApplicationService service = mock(JobApplicationService.class);
        JobApplicationController controller = new JobApplicationController(service);
        JobApplicationResponseDto expected = new JobApplicationResponseDto();
        when(service.softDeleteJobApplication(1L)).thenReturn(expected);
        ResponseEntity<JobApplicationResponseDto> response=controller.softDelete(1L);
        assertEquals(200, response.getStatusCode().value());
        assertSame(expected, response.getBody());
    }
}
