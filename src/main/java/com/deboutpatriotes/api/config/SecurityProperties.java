package com.deboutpatriotes.api.config;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.security")
public record SecurityProperties(String jwtSecret, Duration tokenValidity, List<String> corsOrigins) {
}
