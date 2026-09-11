package com.example.JobTracker.controller;

import com.example.JobTracker.dto.CreateJobApplicationRequestDto;
import com.example.JobTracker.dto.JobApplicationRequestDto;
import com.example.JobTracker.dto.JobApplicationResponseDto;
import com.example.JobTracker.service.JobApplicationService;
import jakarta.validation.Valid;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@EnableJpaAuditing
public class Controller {
    private JobApplicationService Service;
    Controller(JobApplicationService Service){
        this.Service=Service;
    }
    @GetMapping("/{id}")
    public  ResponseEntity<JobApplicationResponseDto> getControllerById( @PathVariable Long id){
        JobApplicationResponseDto Final_Body=Service.GetJobApplicationById(id);
        return ResponseEntity.status(200).body(Final_Body);
    }
    @GetMapping
    public  ResponseEntity<List<JobApplicationResponseDto>> getControllerAll(@RequestParam(defaultValue="0")int page,@RequestParam(defaultValue="10")int size){
        List<JobApplicationResponseDto> Final_Body=Service.GetJobApplications(page,size);
        if(Final_Body==null){
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.status(200).body(Final_Body);
    }
    @PostMapping
    public ResponseEntity<JobApplicationResponseDto> postController(@Valid @RequestBody CreateJobApplicationRequestDto Jard){
        JobApplicationResponseDto Final_Body=Service.createJobApplication(Jard);
        return ResponseEntity.status(201).body(Final_Body);
    }
    @PutMapping("/{id}")
    public ResponseEntity<JobApplicationResponseDto>updateController(@PathVariable Long id,@Valid @RequestBody JobApplicationRequestDto Jard){
        JobApplicationResponseDto Final_Body=Service.updateJobApplication(id,Jard);
        return ResponseEntity.status(200).build();
    }
    @PutMapping("/softdelete/{id}")
    public ResponseEntity<JobApplicationResponseDto> softDeleteController(@PathVariable Long id){
        JobApplicationResponseDto Final_Body=Service.softDeleteJobApplication(id);
        return ResponseEntity.status(200).build();
    }

}