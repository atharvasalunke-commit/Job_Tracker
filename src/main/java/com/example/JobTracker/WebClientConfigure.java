package com.example.JobTracker;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfigure {

    @Value("${scraper.url:http://localhost:8001}")
    private String scraperUrl;

    @Bean
    public WebClient scrapeWebClient(){
        return WebClient.builder().baseUrl(scraperUrl).build();
    }
}
