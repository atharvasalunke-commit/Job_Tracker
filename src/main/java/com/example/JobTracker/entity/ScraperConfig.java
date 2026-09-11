package com.example.JobTracker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
@Entity
@Table(name="scraper_configs")
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

    public void setDomain_name(String domain_name) {
        this.domain_name = domain_name;
    }

    public void setCard_selector(String card_selector) {
        this.card_selector = card_selector;
    }

    public void setTitle_selector(String title_selector) {
        this.title_selector = title_selector;
    }

    public void setCompany_selector(String company_selector) {
        this.company_selector = company_selector;
    }

    public void setLink_selector(String link_selector) {
        this.link_selector = link_selector;
    }

    public void setRequirements_selector(String requirements_selector) {
        this.requirements_selector = requirements_selector;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getDomain_name() {
        return domain_name;
    }

    public String getCard_selector() {
        return card_selector;
    }

    public String getTitle_selector() {
        return title_selector;
    }

    public String getCompany_selector() {
        return company_selector;
    }

    public String getLink_selector() {
        return link_selector;
    }

    public String getRequirements_selector() {
        return requirements_selector;
    }
}
