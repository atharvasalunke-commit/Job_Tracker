package com.example.JobTracker.mapper;

import lombok.RequiredArgsConstructor;
import com.example.JobTracker.dto.*;
import com.example.JobTracker.entity.JobApplication;
import com.example.JobTracker.entity.ScraperConfig;
import com.example.JobTracker.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;
import java.util.List;
@org.mapstruct.Mapper(componentModel="spring")

public interface Mapper {
    JobApplication toEntity2(ScrapedJobDto source);
    JobApplicationResponseDto toResponseDto(JobApplication source);
    List<JobApplicationResponseDto> toListResponseDto(Page<JobApplication> source);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(JobApplicationRequestDto Jard, @MappingTarget JobApplication ja);
    User toUser(AccountRequest source);
    JobApplication toEntity(CreateJobApplicationRequestDto source);
    void updateScrapedConfigs(ScraperConfig source,@MappingTarget ScraperConfig target);
    ScraperConfig toScraperConfigs(ScraperConfigsDto source);
    ScraperConfigsDto toScraperConfigsDto(ScraperConfig source);
}