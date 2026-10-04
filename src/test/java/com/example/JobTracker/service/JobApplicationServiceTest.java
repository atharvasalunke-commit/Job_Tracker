package com.example.JobTracker.service;

import com.example.JobTracker.dto.JobApplicationResponseDto;
import com.example.JobTracker.dto.ScrapedJobDto;
import com.example.JobTracker.entity.JobApplication;
import com.example.JobTracker.entity.User;
import com.example.JobTracker.exception.ResourceNotFound;
import com.example.JobTracker.mapper.Mapper;
import com.example.JobTracker.repository.JobApplicationRepository;
import com.example.JobTracker.validator.JobApplicationValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobApplicationServiceTest {

    @Mock
    private JobApplicationRepository repo;

    @Mock
    private Mapper mapper;

    @Mock
    private AuthService authService;

    @Mock
    private JobApplicationValidator validator;

    @InjectMocks
    private JobApplicationService service;

    @Test
    void createScrapedJobShouldSaveAndReturnResponse() throws Exception {
        User user = new User();
        ScrapedJobDto dto = new ScrapedJobDto();
        dto.setCompany_name("Google");
        dto.setJob_title("Java Developer");
        JobApplication job = new JobApplication();
        JobApplicationResponseDto response = new JobApplicationResponseDto();
        when(authService.getCurrentUser()).thenReturn(user);
        when(mapper.toEntity2(dto)).thenReturn(job);
        when(validator.generateJobCode(job)).thenReturn("abc");
        when(validator.isDuplicate("abc", user)).thenReturn(false);
        when(repo.save(job)).thenReturn(job);
        when(mapper.toResponseDto(job)).thenReturn(response);
        JobApplicationResponseDto result = service.createScrapedJob(dto);
        assertSame(response, result);
        verify(repo).save(job);
    }

    @Test
    void createJobApplicationShouldSaveAndReturnResponse() throws Exception {
        User user = new User();
        JobApplication job = new JobApplication();
        JobApplicationResponseDto response = new JobApplicationResponseDto();
        var dto = new com.example.JobTracker.dto.CreateJobApplicationRequestDto();
        when(authService.getCurrentUser()).thenReturn(user);
        when(mapper.toEntity(dto)).thenReturn(job);
        when(validator.generateJobCode(job)).thenReturn("abc");
        when(validator.isDuplicate("abc", user)).thenReturn(false);
        when(repo.save(job)).thenReturn(job);
        when(mapper.toResponseDto(job)).thenReturn(response);
        JobApplicationResponseDto result = service.createJobApplication(dto);
        assertSame(response, result);
        verify(repo).save(job);
    }

    @Test
    void getByIdShouldReturnResponse() {
        User user = new User();
        JobApplication job = new JobApplication();
        JobApplicationResponseDto response = new JobApplicationResponseDto();
        when(authService.getCurrentUser()).thenReturn(user);
        when(repo.findByIdAndUser(1L, user)).thenReturn(Optional.of(job));
        when(mapper.toResponseDto(job)).thenReturn(response);
        JobApplicationResponseDto result = service.GetJobApplicationById(1L);
        assertSame(response, result);
    }

    @Test
    void getByIdShouldThrowWhenNotFound() {
        User user = new User();
        when(authService.getCurrentUser()).thenReturn(user);
        when(repo.findByIdAndUser(1L, user)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFound.class, () -> service.GetJobApplicationById(1L));
    }
}