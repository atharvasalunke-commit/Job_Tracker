package com.example.JobTracker.service;

import com.example.JobTracker.dto.CreateJobApplicationRequestDto;
import com.example.JobTracker.dto.JobApplicationRequestDto;
import com.example.JobTracker.dto.JobApplicationResponseDto;
import com.example.JobTracker.entity.job_applications;
import com.example.JobTracker.entity.users;
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
    public users getCurrentUser(){
        return (users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    public JobApplicationResponseDto CreateJobApplication(CreateJobApplicationRequestDto Jard){
        users user=getCurrentUser();
        job_applications target=mapper.toEntity2(Jard);
        target.setCreated_at();
        target.setUpdated_at();
        target.setIs_deleted(0);
        target.setUser(user);
        job_applications Body=Repo.save(target);
        JobApplicationResponseDto Response= mapper.toResponseDto(Body);
        Response.setMessage("Created Job Application");
        return Response;
    }
    public JobApplicationResponseDto GetJobApplicationById(Long id){
        users user=getCurrentUser();
        Optional<job_applications> job_application=Repo.findByIdAndUser(id,user);
        if(job_application.isPresent()) {
            job_applications temp = job_application.get();
            if(temp.getIs_deleted()==1){
               throw new ResourceNotFound("This job aplication had been deleted");
            }
            JobApplicationResponseDto Response = mapper.toResponseDto(temp);
            Response.setMessage("Got Job Application");
            return Response;
        }
        throw new ResourceNotFound("This job aplication doesn't exist");
    }
    public List<JobApplicationResponseDto> GetJobApplications(int page,int size){
        Pageable pageable = PageRequest.of(page,size);
        users user=getCurrentUser();
        Page<job_applications>job_application=Repo.findAllActiveByUser(user,pageable);
        if(!job_application.isEmpty()) {
           List<JobApplicationResponseDto> Response = mapper.toListResponseDto(job_application);
            return Response;
        }
        throw new ResourceNotFound("Could not load any applications");
    }
    public JobApplicationResponseDto updateJobApplication(Long id, JobApplicationRequestDto Jard){
        users user=getCurrentUser();
        Optional<job_applications> target=Repo.findByIdAndUser(id,user);
        if(target.isPresent()){
            job_applications Final_Target=target.get();
            if(Final_Target.getIs_deleted()==1){
                throw new ResourceNotFound("This job aplication had been deleted");
            }
            mapper.updateEntity(Jard,Final_Target);
            Final_Target.setUpdated_at();
            job_applications Body=Repo.save(Final_Target);
            JobApplicationResponseDto Response= mapper.toResponseDto(Body);
            return Response;
        }
        throw new ResourceNotFound("This job aplication doesn't exist");
    }
    public JobApplicationResponseDto softDeleteJobApplication(Long id){
        users user=getCurrentUser();
        Optional<job_applications> target=Repo.findByIdAndUser(id,user);
        if(target.isPresent()){
            job_applications Final_Target=target.get();
            Final_Target.setIs_deleted(1);
            job_applications Body=Repo.save(Final_Target);
            JobApplicationResponseDto Response= mapper.toResponseDto(Body);
            return Response;
        }
        throw new ResourceNotFound("This job aplication doesn't exist");
    }
}
