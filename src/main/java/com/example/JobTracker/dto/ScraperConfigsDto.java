package com.example.JobTracker.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class ScraperConfigsDto {
    @NotBlank
    private String card_selector;
    @NotBlank
    private String title_selector;
    @NotBlank
    private String company_selector;
    @NotBlank
    private String link_selector;
    @NotBlank
    private String requirements_selector;
}
