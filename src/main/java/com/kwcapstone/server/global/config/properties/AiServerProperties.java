package com.kwcapstone.server.global.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "ai.server")
public class AiServerProperties {
    private String baseUrl;
    private String token;
}
