package com.example.JobTracker;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration

public class WebClientConfigure {
@Bean

    public WebClient scrapeWebClient(){
        return WebClient.builder().
                baseUrl("http://localhost:8001").build();
}
}
