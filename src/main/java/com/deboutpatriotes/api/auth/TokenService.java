package com.deboutpatriotes.api.auth;

import java.time.Instant;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.deboutpatriotes.api.config.SecurityConfig;
import com.deboutpatriotes.api.config.SecurityProperties;

@Service
class TokenService {

    private final JwtEncoder encoder;
    private final SecurityProperties properties;

    TokenService(JwtEncoder encoder, SecurityProperties properties) {
        this.encoder = encoder;
        this.properties = properties;
    }

    AuthDtos.TokenResponse issue(AdminUser user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(properties.tokenValidity());
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("debout-patriotes-api")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(String.valueOf(user.getId()))
                .claim("email", user.getEmail())
                .claim("name", user.getDisplayName())
                .claim("scope", SecurityConfig.ADMIN_SCOPE)
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new AuthDtos.TokenResponse(token, expiresAt, AuthDtos.UserResponse.of(user));
    }
}
