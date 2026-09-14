package com.example.JobTracker.service;

import com.example.JobTracker.dto.CreateJobApplicationRequestDto;
import com.example.JobTracker.entity.JobApplication;
import com.example.JobTracker.entity.User;
import com.example.JobTracker.exception.ResourceAlreadyExists;
import com.example.JobTracker.mapper.Mapper;
import com.example.JobTracker.repository.JobApplicationRepository;
import com.example.JobTracker.validator.JobApplicationValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.eq;

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
    private JobApplicationService jobApplicationService;

    @Test
    void checkIfDuplicateJobThrowsException() throws Exception {
        CreateJobApplicationRequestDto request = new CreateJobApplicationRequestDto();
        JobApplication mappedEntity = new JobApplication();
        User currentUser = new User();
        when(mapper.toEntity(request)).thenReturn(mappedEntity);
        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(validator.generateJobCode(any())).thenReturn("hash123");
        when(validator.isDuplicate("hash123", currentUser)).thenReturn(true);

        assertThrows(ResourceAlreadyExists.class, () -> jobApplicationService.createJobApplication(request));
        verify(repo, never()).save(any());
    }
}
