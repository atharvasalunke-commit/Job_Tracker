package com.example.JobTracker.service;

import com.example.JobTracker.dto.CreateJobApplicationRequestDto;
import com.example.JobTracker.dto.JobApplicationRequestDto;
import com.example.JobTracker.dto.JobApplicationResponseDto;
import com.example.JobTracker.dto.ScrapedJobDto;
import com.example.JobTracker.entity.JobApplication;
import com.example.JobTracker.entity.User;
import com.example.JobTracker.globalexception.ResourceNotFound;
import com.example.JobTracker.mapper.Mapper;
import com.example.JobTracker.repository.Repository2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Service
public class JobApplicationService {
    private Repository2 Repo;
    private Mapper mapper;
    public JobApplicationService(Repository2 Repo, Mapper mapper){
        this.Repo=Repo;
        this.mapper=mapper;
    }
    public User getCurrentUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    public JobApplicationResponseDto createJobApplication(CreateJobApplicationRequestDto dto){
        User user = getCurrentUser();
        JobApplication target = mapper.toEntity(dto);
        target.setCreated_at();
        target.setUpdated_at();
        target.setIs_deleted(0);
        target.setUser(user);
        target.setStatus("Not applied");
        JobApplication Body = Repo.save(target);
        JobApplicationResponseDto Response = mapper.toResponseDto(Body);
        Response.setMessage("Created Job Application");
        return Response;
    }

    public JobApplicationResponseDto createScrapedJob(ScrapedJobDto Sjd){
        User user=getCurrentUser();
        JobApplication target=mapper.toEntity2(Sjd);
        target.setCreated_at();
        target.setUpdated_at();
        target.setIs_deleted(0);
        target.setUser(user);
        target.setStatus("Not applied");
        JobApplication Body=Repo.save(target);
        JobApplicationResponseDto Response= mapper.toResponseDto(Body);
        Response.setMessage("Created Job Application");
        return Response;
    }
    public JobApplicationResponseDto GetJobApplicationById(Long id){
        User user=getCurrentUser();
        Optional<JobApplication> job_application=Repo.findByIdAndUser(id,user);
        if(job_application.isPresent()) {
            JobApplication temp = job_application.get();
            if(temp.getIs_deleted()==1){
               throw new ResourceNotFound("This job application has been deleted");
            }
            JobApplicationResponseDto Response = mapper.toResponseDto(temp);
            Response.setMessage("Got Job Application");
            return Response;
        }
        throw new ResourceNotFound("This job application doesn't exist");
    }
    public List<JobApplicationResponseDto> GetJobApplications(int page,int size){
        Pageable pageable = PageRequest.of(page,size);
        User user=getCurrentUser();
        Page<JobApplication>job_application=Repo.findAllActiveByUser(user,pageable);
        if(!job_application.isEmpty()) {
           List<JobApplicationResponseDto> Response = mapper.toListResponseDto(job_application);
            return Response;
        }
        throw new ResourceNotFound("Could not load any applications");
    }
    public JobApplicationResponseDto updateJobApplication(Long id, JobApplicationRequestDto Jard){
        User user=getCurrentUser();
        Optional<JobApplication> target=Repo.findByIdAndUser(id,user);
        if(target.isPresent()){
            JobApplication Final_Target=target.get();
            if(Final_Target.getIs_deleted()==1){
                throw new ResourceNotFound("This job application has been deleted");
            }
            mapper.updateEntity(Jard,Final_Target);
            Final_Target.setUpdated_at();
            JobApplication Body=Repo.save(Final_Target);
            JobApplicationResponseDto Response= mapper.toResponseDto(Body);
            return Response;
        }
        throw new ResourceNotFound("This job application doesn't exist");
    }
    public JobApplicationResponseDto softDeleteJobApplication(Long id){
        User user=getCurrentUser();
        Optional<JobApplication> target=Repo.findByIdAndUser(id,user);
        if(target.isPresent()){
            JobApplication Final_Target=target.get();
            Final_Target.setIs_deleted(1);
            JobApplication Body=Repo.save(Final_Target);
            JobApplicationResponseDto Response= mapper.toResponseDto(Body);
            return Response;
        }
        throw new ResourceNotFound("This job application doesn't exist");
    }
}
