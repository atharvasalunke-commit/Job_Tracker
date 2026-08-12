package com.example.JobTracker.mapper;

import com.example.JobTracker.dto.JobApplicationRequestDto;
import com.example.JobTracker.dto.JobApplicationResponseDto;
import com.example.JobTracker.entity.job_applications;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel="spring")
public interface JobApplicationMapper {
    job_applications toEntity(JobApplicationRequestDto source);

    JobApplicationResponseDto toResponseDto(job_applications source);
    List<JobApplicationResponseDto> toListResponseDto(List<job_applications> source);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(JobApplicationRequestDto Jard, @MappingTarget job_applications ja);
}