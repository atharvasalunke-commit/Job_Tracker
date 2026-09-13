package com.example.JobTracker.service;

import com.example.JobTracker.client.GeminiClient;
import com.example.JobTracker.dto.PythonScraperRequest;
import com.example.JobTracker.dto.ScraperConfigsDto;
import com.example.JobTracker.dto.UserRequestDto;
import com.example.JobTracker.entity.ScraperConfig;
import com.example.JobTracker.exception.InvalidUrlException;
import com.example.JobTracker.exception.ScraperConfigException;
import com.example.JobTracker.mapper.Mapper;
import com.example.JobTracker.repository.ScraperConfigRepository;
import com.example.JobTracker.strategy.JobSiteStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class ScraperConfigService {
    private final ScraperConfigRepository repository;
    private final Mapper mapper;
    private final GeminiClient geminiClient;
    private final List<JobSiteStrategy> strategies;

    public ScraperConfig configure(ScraperConfig config) {
        Optional<ScraperConfig> existingConfig = repository.findByDomainName(config.getDomain_name());
        if (existingConfig.isPresent()) {
            ScraperConfig existing = existingConfig.get();
            mapper.updateScrapedConfigs(config, existing);
            return repository.save(existing);
        }
        return repository.save(config);
    }

    public PythonScraperRequest prepareRequest(UserRequestDto request) {
        JobSiteStrategy strategy = getStrategy(request.getDomain_name());
        String url = strategy.buildSearchUrl(request.getUser_skills());
        Optional<ScraperConfig> savedConfig = repository.findByDomainName(request.getDomain_name());
        ScraperConfigsDto rules;
        if (savedConfig.isPresent()) {
            rules = mapper.toScraperConfigsDto(savedConfig.get());
        } else {
            rules = generateAndSaveConfig(request.getDomain_name(), url);
        }
        PythonScraperRequest scraperRequest = new PythonScraperRequest();
        scraperRequest.setUrl(url);
        scraperRequest.setRules(rules);
        scraperRequest.setUser_skills(request.getUser_skills());
        scraperRequest.setJob_type(request.getJob_type());
        return scraperRequest;
    }

    public void regenerateConfig(String domain, String applicationUrl) {
        getStrategy(domain);
        ScraperConfigsDto generated = geminiClient.generateConfig(applicationUrl);
        ScraperConfig config = mapper.toScraperConfigs(generated);
        config.setDomain_name(domain);
        Optional<ScraperConfig> existingConfig = repository.findByDomainName(domain);
        if (existingConfig.isPresent()) {
            ScraperConfig existing = existingConfig.get();
            mapper.updateScrapedConfigs(config, existing);
            repository.save(existing);
        } else {
            repository.save(config);
        }
    }
    private ScraperConfigsDto generateAndSaveConfig(String domain, String url) {
        try {
            ScraperConfigsDto generated = geminiClient.generateConfig(url);
            ScraperConfig config = mapper.toScraperConfigs(generated);
            config.setDomain_name(domain);
            repository.save(config);
            return generated;
        }
        catch (Exception e) {
            if (e instanceof ScraperConfigException) {
                throw e;
            }
            throw new ScraperConfigException("Failed to create scraper configuration for "
                            + domain, e);
        }
    }
    private JobSiteStrategy getStrategy(String domain) {
        for (JobSiteStrategy strategy : strategies) {
            if (strategy.getDomain().equalsIgnoreCase(domain)) {
                return strategy;
            }
        }
        throw new InvalidUrlException("Unsupported job site: " + domain);
    }
}
