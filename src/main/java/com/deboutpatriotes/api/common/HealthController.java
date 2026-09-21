package com.deboutpatriotes.api.common;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Sonde de disponibilité pour l'hébergeur. */
@RestController
class HealthController {

    @GetMapping("/api/health")
    Map<String, String> health() {
        return Map.of("status", "ok");
    }
}
