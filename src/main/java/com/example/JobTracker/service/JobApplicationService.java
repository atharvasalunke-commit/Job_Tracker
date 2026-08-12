package com.example.JobTracker.service;

import com.example.JobTracker.dto.JobApplicationRequestDto;
import com.example.JobTracker.dto.JobApplicationResponseDto;
import com.example.JobTracker.entity.job_applications;
import com.example.JobTracker.mapper.JobApplicationMapper;
import com.example.JobTracker.repository.Repository2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobApplicationService {
    private Repository2 Repo;
    private JobApplicationMapper mapper;
    public JobApplicationService(Repository2 Repo,JobApplicationMapper mapper){
        this.Repo=Repo;
        this.mapper=mapper;;
    }
    public JobApplicationResponseDto CreateJobApplication(JobApplicationRequestDto Jard){
        job_applications target=mapper.toEntity(Jard);
        target.setCreated_at();
        target.setUpdated_at();
        target.setIs_deleted(0);
        job_applications Body=Repo.save(target);
        JobApplicationResponseDto Response= mapper.toResponseDto(Body);
        Response.setMessage("Created Job Application");
        return Response;
    }
    public JobApplicationResponseDto GetJobApplicationById(Long id){
        Optional<job_applications> job_application=Repo.findById(id);
        if(job_application.isPresent()) {
            job_applications temp = job_application.get();
            if(temp.getIs_deleted()==1){
                return null;
            }
            JobApplicationResponseDto Response = mapper.toResponseDto(temp);
            Response.setMessage("Get Job Application");
            return Response;
        }
      return null;
    }
    public List<JobApplicationResponseDto> GetJobApplications(){
        List<job_applications> job_application=Repo.findAllActive();
        if(!job_application.isEmpty()) {
           List<JobApplicationResponseDto> Response = mapper.toListResponseDto(job_application);
            return Response;
        }
        return null;
    }
    public JobApplicationResponseDto updateJobApplication(Long id,JobApplicationRequestDto Jard){
        Optional<job_applications> target=Repo.findById(id);
        if(target.isPresent()){
            job_applications Final_Target=target.get();
            if(Final_Target.getIs_deleted()==1){
                return null;
            }
            mapper.updateEntity(Jard,Final_Target);
            Final_Target.setUpdated_at();
            job_applications Body=Repo.save(Final_Target);
            JobApplicationResponseDto Response= mapper.toResponseDto(Body);
            return Response;
        }
            return null;
    }
    public JobApplicationResponseDto softDeleteJobApplication(Long id){
        Optional<job_applications> target=Repo.findById(id);
        if(target.isPresent()){
            job_applications Final_Target=target.get();
            Final_Target.setIs_deleted(1);
            job_applications Body=Repo.save(Final_Target);
            JobApplicationResponseDto Response= mapper.toResponseDto(Body);
            return Response;
        }
        return null;
    }
}
