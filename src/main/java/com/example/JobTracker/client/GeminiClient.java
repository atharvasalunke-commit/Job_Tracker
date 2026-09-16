package com.example.JobTracker.client;

import com.example.JobTracker.dto.ScraperConfigsDto;
import com.example.JobTracker.exception.ScraperConfigException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.Duration;
import java.util.Collections;

@Component
@RequiredArgsConstructor

public class GeminiClient {
    private final WebClient webClient;
    private java.time.Instant lastRequestTime = java.time.Instant.MIN;

    public ScraperConfigsDto generateConfig(String applicationUrl) {
        long secondsSinceLastRequest = java.time.Duration.between(lastRequestTime, java.time.Instant.now()).getSeconds();
        if (secondsSinceLastRequest < 60) {
            long waitTime = 60 - secondsSinceLastRequest;
            throw new ScraperConfigException("Please wait " + waitTime + " more seconds before scraping again to prevent API rate limits.");
        }
        
        try {
            lastRequestTime = java.time.Instant.now();

            ScraperConfigsDto response = webClient.post().uri("/api/generate-config").bodyValue(Collections.singletonMap("url", applicationUrl)).retrieve().bodyToMono(ScraperConfigsDto.class).timeout(Duration.ofSeconds(600)).block();
            if (response == null) {
                throw new ScraperConfigException("Python service returned an empty scraper configuration");
            }
            return response;
} catch (ScraperConfigException e) {
            throw e;
} catch (Exception e) {
            throw new ScraperConfigException("Failed to generate scraper configuration", e);
        }
    }
}