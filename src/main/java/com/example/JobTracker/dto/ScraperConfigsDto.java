package com.example.JobTracker.dto;

import jakarta.validation.constraints.NotBlank;

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

    public String getCard_selector() {
        return card_selector;
    }

    public void setCard_selector(String card_selector) {
        this.card_selector = card_selector;
    }

    public String getTitle_selector() {
        return title_selector;
    }

    public void setTitle_selector(String title_selector) {
        this.title_selector = title_selector;
    }

    public String getCompany_selector() {
        return company_selector;
    }

    public void setCompany_selector(String company_selector) {
        this.company_selector = company_selector;
    }

    public String getLink_selector() {
        return link_selector;
    }

    public void setLink_selector(String link_selector) {
        this.link_selector = link_selector;
    }

    public String getRequirements_selector() {
        return requirements_selector;
    }

    public void setRequirements_selector(String requirements_selector) {
        this.requirements_selector = requirements_selector;
    }
}
