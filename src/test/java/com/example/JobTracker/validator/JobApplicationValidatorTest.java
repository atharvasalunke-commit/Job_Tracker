package com.example.JobTracker.validator;

import com.example.JobTracker.Sha256.BasicSha256;
import com.example.JobTracker.entity.JobApplication;
import com.example.JobTracker.exception.ResourceNotFound;
import com.example.JobTracker.repository.JobApplicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JobApplicationValidatorTest {

    @Mock private JobApplicationRepository repo;
    @Mock private BasicSha256 sha256;
    @InjectMocks private JobApplicationValidator validator;

    @Test
    void checkIfDeletedJobThrowsException() {
        JobApplication deletedJob = new JobApplication();
        deletedJob.setIs_deleted(1);
        assertThrows(ResourceNotFound.class, () -> validator.validateExistsAndNotDeleted(deletedJob));

        JobApplication activeJob = new JobApplication();
        activeJob.setIs_deleted(0);
        assertDoesNotThrow(() -> validator.validateExistsAndNotDeleted(activeJob));
    }
}
