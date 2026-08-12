package com.example.JobTracker.controller;

import com.example.JobTracker.dto.JobApplicationRequestDto;
import com.example.JobTracker.dto.JobApplicationResponseDto;
import com.example.JobTracker.service.JobApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class Controller {
    private JobApplicationService Service;
    Controller(JobApplicationService Service){
        this.Service=Service;
    }
    @GetMapping("/{id}")
    public  ResponseEntity<JobApplicationResponseDto> getControllerById(@PathVariable Long id){
        JobApplicationResponseDto Final_Body=Service.GetJobApplicationById(id);
        if(Final_Body==null){
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.status(200).body(Final_Body);
    }
    @GetMapping
    public  ResponseEntity<List<JobApplicationResponseDto>> getControllerAll(){
        List<JobApplicationResponseDto> Final_Body=Service.GetJobApplications();
        if(Final_Body==null){
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.status(200).body(Final_Body);
    }
    @PostMapping
    public ResponseEntity<JobApplicationResponseDto> postController(@RequestBody JobApplicationRequestDto Jard){
        JobApplicationResponseDto Final_Body=Service.CreateJobApplication(Jard);
        return ResponseEntity.status(201).body(Final_Body);
    }
    @PutMapping("/{id}")
    public ResponseEntity<JobApplicationResponseDto>updateController(@PathVariable Long id,@RequestBody JobApplicationRequestDto Jard){
        JobApplicationResponseDto Final_Body=Service.updateJobApplication(id,Jard);
        if(Final_Body==null){
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.status(204).build();
    }
    @PutMapping("/softdelete/{id}")
    public ResponseEntity<JobApplicationResponseDto> softDeleteController(@PathVariable Long id){
        JobApplicationResponseDto Final_Body=Service.softDeleteJobApplication(id);
        if(Final_Body==null){
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.status(204).build();
    }

}