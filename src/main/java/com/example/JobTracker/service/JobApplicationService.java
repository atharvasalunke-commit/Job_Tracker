package com.example.JobTracker.service;

import com.example.JobTracker.exception.ResourceAlreadyExists;
import lombok.RequiredArgsConstructor;
import com.example.JobTracker.dto.*;
import com.example.JobTracker.entity.JobApplication;
import com.example.JobTracker.entity.User;
import com.example.JobTracker.exception.ResourceNotFound;
import com.example.JobTracker.mapper.Mapper;
import com.example.JobTracker.repository.JobApplicationRepository;
import com.example.JobTracker.validator.JobApplicationValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class JobApplicationService {
    private final JobApplicationRepository repo;
    private final Mapper mapper;
    private final AuthService authService;
    private final JobApplicationValidator validator;

    public JobApplicationResponseDto createJobApplication(CreateJobApplicationRequestDto dto) throws Exception {
        JobApplication target = mapper.toEntity(dto);
        prepareNewApplication(target, authService.getCurrentUser());
        if (validator.isDuplicate(target.getCode(), target.getUser())) {
            throw new ResourceAlreadyExists("You have already saved this job application.");
        }
        JobApplication body = repo.save(target);
        JobApplicationResponseDto response = mapper.toResponseDto(body);
        response.setMessage("Created Job Application");
        return response;
    }

    public JobApplicationResponseDto createScrapedJob(ScrapedJobDto Sjd) throws Exception {
        JobApplication target = mapper.toEntity2(Sjd);
        prepareNewApplication(target, authService.getCurrentUser());
        if (validator.isDuplicate(target.getCode(), target.getUser())) {
            throw new com.example.JobTracker.exception.ResourceAlreadyExists("You have already saved this job application.");
        }
        JobApplication body = repo.save(target);
        JobApplicationResponseDto response = mapper.toResponseDto(body);
        response.setMessage("Created Job Application");
        return response;
    }

    public JobApplicationResponseDto GetJobApplicationById(Long id) {
        Optional<JobApplication> jobApplication = repo.findByIdAndUser(id, authService.getCurrentUser());
        if (jobApplication.isEmpty()) {
            throw new ResourceNotFound("This job application doesn't exist");
        }
        JobApplication temp = jobApplication.get();
        validator.validateExistsAndNotDeleted(temp);
        JobApplicationResponseDto response = mapper.toResponseDto(temp);
        response.setMessage("Got Job Application");
        return response;
    }

    public List<JobApplicationResponseDto> GetJobApplications(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created_at"));
        Page<JobApplication> job_application = repo.findAllActiveByUser(authService.getCurrentUser(), pageable);
        return mapper.toListResponseDto(job_application);
    }

    public JobApplicationResponseDto updateJobApplication(Long id, JobApplicationRequestDto Jard)throws Exception {
        Optional<JobApplication> jobApplication = repo.findByIdAndUser(id, authService.getCurrentUser());
        if (jobApplication.isEmpty()) {
            throw new ResourceNotFound("This job application doesn't exist");
        }
        JobApplication target = jobApplication.get();
        validator.validateExistsAndNotDeleted(target);
        mapper.updateEntity(Jard, target);
        target.setUpdated_at();
        target.setCode(validator.generateJobCode(target));
        JobApplication body = repo.save(target);
        return mapper.toResponseDto(body);
    }

    public JobApplicationResponseDto softDeleteJobApplication(Long id) {
        Optional<JobApplication> jobApplication = repo.findByIdAndUser(id, authService.getCurrentUser());
        if (jobApplication.isEmpty()) {
            throw new ResourceNotFound("This job application doesn't exist");
        }
        JobApplication target = jobApplication.get();
        target.setIs_deleted(1);
        JobApplication body = repo.save(target);
        return mapper.toResponseDto(body);
    }
    private void prepareNewApplication(JobApplication target, User user) throws Exception {
        target.setCreated_at();
        target.setUpdated_at();
        target.setIs_deleted(0);
        target.setUser(user);
        target.setStatus("Not applied");
        String code = validator.generateJobCode(target);
        target.setCode(code);
    }
}
