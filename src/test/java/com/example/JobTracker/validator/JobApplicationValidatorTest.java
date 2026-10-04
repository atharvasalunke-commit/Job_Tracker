package com.example.JobTracker.validator;

import com.example.JobTracker.Sha256.BasicSha256;
import com.example.JobTracker.entity.JobApplication;
import com.example.JobTracker.entity.User;
import com.example.JobTracker.exception.ResourceNotFound;
import com.example.JobTracker.repository.JobApplicationRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JobApplicationValidatorTest {

    @Test
    void generateJobCodeShouldHashApplicationUrl() throws Exception {
        JobApplicationRepository repo = mock(JobApplicationRepository.class);
        BasicSha256 sha256 = new BasicSha256();
        JobApplicationValidator validator = new JobApplicationValidator(repo, sha256);
        JobApplication job = new JobApplication();
        job.setApplication_url("https://example.com/job");
        String result = validator.generateJobCode(job);
        assertEquals(sha256.toSha256("https://example.com/job"), result);
    }
    @Test
    void isDuplicateShouldDelegateToRepository() {
        JobApplicationRepository repo = mock(JobApplicationRepository.class);
        JobApplicationValidator validator = new JobApplicationValidator(repo, mock(BasicSha256.class));
        User user = new User();
        when(repo.existsByCodeAndUser("abc", user)).thenReturn(true);
        assertTrue(validator.isDuplicate("abc", user));
        verify(repo).existsByCodeAndUser("abc", user);
    }

    @Test
    void validateExistsAndNotDeletedShouldThrowWhenDeleted() {
        JobApplicationRepository repo = mock(JobApplicationRepository.class);
        JobApplicationValidator validator = new JobApplicationValidator(repo, mock(BasicSha256.class));
        JobApplication job = new JobApplication();
        job.setIs_deleted(1);
        assertThrows(ResourceNotFound.class, () -> validator.validateExistsAndNotDeleted(job));
    }

    @Test
    void validateExistsAndNotDeletedShouldDoNothingWhenActive() {
        JobApplicationRepository repo = mock(JobApplicationRepository.class);
        JobApplicationValidator validator = new JobApplicationValidator(repo, mock(BasicSha256.class));
        JobApplication job = new JobApplication();
        job.setIs_deleted(0);
        assertDoesNotThrow(() -> validator.validateExistsAndNotDeleted(job));
    }
}
