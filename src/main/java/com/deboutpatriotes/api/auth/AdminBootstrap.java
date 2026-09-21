package com.deboutpatriotes.api.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.deboutpatriotes.api.config.AdminProperties;

/** Crée le premier compte administrateur à partir de `app.admin.*` si la table est vide. */
@Component
class AdminBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

    private final AdminUserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final AdminProperties properties;

    AdminBootstrap(AdminUserRepository users, PasswordEncoder passwordEncoder, AdminProperties properties) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (users.count() > 0) {
            return;
        }
        if (properties.password() == null || properties.password().length() < 8) {
            log.warn("Aucun administrateur en base : définissez ADMIN_EMAIL et ADMIN_PASSWORD (8 caractères minimum) "
                    + "pour créer le premier compte.");
            return;
        }
        AdminUser admin = new AdminUser();
        admin.setEmail(properties.email().trim().toLowerCase());
        admin.setDisplayName(properties.displayName());
        admin.setPasswordHash(passwordEncoder.encode(properties.password()));
        users.save(admin);
        log.info("Compte administrateur initial créé : {}", admin.getEmail());
    }
}
