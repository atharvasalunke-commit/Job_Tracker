package com.example.JobTracker.entity;

import lombok.Getter;
import lombok.Setter;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
@Entity
@Table(name="scraper_configs")
@Getter
@Setter
public class ScraperConfig {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String domain_name;
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
