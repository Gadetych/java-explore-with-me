package ru.practicum.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.stats.client.StatClient;

@Configuration
public class MainServiceConfig {
    @Value("${stat.client.base-url}")
    private String statClientBaseUrl;
    @Value("${stat.client.timeout}")
    private int timeout;

    @Bean
    public StatClient statClient() {
        return new StatClient(statClientBaseUrl, timeout);
    }
}
