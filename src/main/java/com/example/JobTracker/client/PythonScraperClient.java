package com.example.JobTracker.client;

import com.example.JobTracker.dto.PythonScraperRequest;
import com.example.JobTracker.dto.ScrapedJobDto;
import com.example.JobTracker.exception.PythonScraperException;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
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
            ScrapedJobDto[] response=webClient
                            .post()
                            .uri("/api/scrape")
                            .bodyValue(request)
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse
                                    .bodyToMono(String.class).map(body -> new PythonScraperException("Python scraper returned " + clientResponse.statusCode() + ": " + body)))
                            .bodyToMono(ScrapedJobDto[].class)
                            .timeout(Duration.ofSeconds(300))
                            .block();
            if (response == null) {
                throw new PythonScraperException(
                        "Python scraper returned no response"
                );
            }

            return Arrays.asList(response);

        } catch (PythonScraperException e) {
            throw e;

        } catch (Exception e) {
            throw new PythonScraperException(
                    "Could not connect to Python scraper at http://127.0.0.1:8001",
                    e
            );
        }
    }
}