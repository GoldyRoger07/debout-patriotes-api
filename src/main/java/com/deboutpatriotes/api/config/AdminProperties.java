package com.deboutpatriotes.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Compte créé au premier démarrage (voir {@code AdminBootstrap}). */
@ConfigurationProperties("app.admin")
public record AdminProperties(String email, String password, String displayName) {
}
