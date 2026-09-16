package com.example.JobTracker.validator;

import com.example.JobTracker.Sha256.BasicSha256;
import com.example.JobTracker.entity.JobApplication;
import com.example.JobTracker.entity.User;
import com.example.JobTracker.exception.ResourceNotFound;
import com.example.JobTracker.repository.JobApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor

public class JobApplicationValidator {
    private final JobApplicationRepository repo;
    private final BasicSha256 sha256;

    public String generateJobCode(JobApplication target) throws Exception {
        String code = target.getApplication_url();
        return sha256.toSha256(code);
    }

    public boolean isDuplicate(String code, User user) {
        return repo.existsByCodeAndUser(code, user);
    }

    public void validateExistsAndNotDeleted(JobApplication target) {
        if (target.getIs_deleted() == 1) {
            throw new ResourceNotFound("This job application has been deleted");
        }
    }
}
