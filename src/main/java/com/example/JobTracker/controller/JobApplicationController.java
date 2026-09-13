package com.example.JobTracker.controller;

import com.example.JobTracker.dto.CreateJobApplicationRequestDto;
import com.example.JobTracker.dto.JobApplicationRequestDto;
import com.example.JobTracker.dto.JobApplicationResponseDto;
import com.example.JobTracker.service.JobApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobApplicationController {
    private final JobApplicationService jobApplicationService;

    @GetMapping("/{id}")
    public ResponseEntity<JobApplicationResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(jobApplicationService.GetJobApplicationById(id));
    }

    @GetMapping
    public ResponseEntity<List<JobApplicationResponseDto>> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(jobApplicationService.GetJobApplications(page, size));
    }

    @PostMapping
    public ResponseEntity<JobApplicationResponseDto> create(@Valid @RequestBody CreateJobApplicationRequestDto request) throws Exception {
        return ResponseEntity.status(201).body(jobApplicationService.createJobApplication(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobApplicationResponseDto> update(@PathVariable Long id, @Valid @RequestBody JobApplicationRequestDto request) throws Exception {
        return ResponseEntity.ok(jobApplicationService.updateJobApplication(id, request));
    }

    @PutMapping("/softdelete/{id}")
    public ResponseEntity<JobApplicationResponseDto> softDelete(@PathVariable Long id) {
        return ResponseEntity.ok(jobApplicationService.softDeleteJobApplication(id));
    }
}
