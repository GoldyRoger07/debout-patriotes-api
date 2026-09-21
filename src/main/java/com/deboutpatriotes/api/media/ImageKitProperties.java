package com.deboutpatriotes.api.media;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Identifiants du compte ImageKit.io (Tableau de bord → Developer options → API keys).
 * La clé privée ne quitte jamais le serveur : le navigateur téléverse via l'API.
 */
@ConfigurationProperties("app.imagekit")
public record ImageKitProperties(String publicKey, String privateKey, String urlEndpoint, String folder) {

    public boolean configured() {
        return privateKey != null && !privateKey.isBlank();
    }
}
