package com.example.JobTracker.client;

import com.example.JobTracker.dto.PythonScraperRequest;
import com.example.JobTracker.dto.ScrapedJobDto;
import com.example.JobTracker.exception.PythonScraperException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor

public class PythonScraperClient {
    private final WebClient webClient;

    public List<ScrapedJobDto> scrape(PythonScraperRequest request) {
        try {
            ScrapedJobDto[] response = webClient.post().uri("/api/scrape").bodyValue(request).retrieve().bodyToMono(ScrapedJobDto[].class).timeout(Duration.ofSeconds(120)).block();
            if (response == null) {
                throw new PythonScraperException("Python scraper returned no response");
            }
            return Arrays.asList(response);
} catch (PythonScraperException e) {
            throw e;
} catch (Exception e) {
            throw new PythonScraperException("Failed to communicate with Python scraper", e);
        }
    }
}