package com.kwcapstone.server.global.config;

import com.kwcapstone.server.global.config.properties.AiServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(AiServerProperties.class)
public class AiServerConfig {
    @Bean
    public RestClient aiRestClient(AiServerProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader("x-api-token", properties.getToken())
                .build();
    }
}
