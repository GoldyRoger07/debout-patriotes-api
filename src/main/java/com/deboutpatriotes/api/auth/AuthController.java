package com.deboutpatriotes.api.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.deboutpatriotes.api.common.BadRequestException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
class AuthController {

    private final AdminUserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokens;
    /** Empreinte factice : la vérification coûte autant, que le compte existe ou non. */
    private final String dummyHash;

    AuthController(AdminUserRepository users, PasswordEncoder passwordEncoder, TokenService tokens) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.tokens = tokens;
        this.dummyHash = passwordEncoder.encode("dummy-password-for-timing");
    }

    @PostMapping("/login")
    AuthDtos.TokenResponse login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        AdminUser user = users.findByEmailIgnoreCase(request.email().trim()).orElse(null);
        boolean matches = passwordEncoder.matches(request.password(), user != null ? user.getPasswordHash() : dummyHash);
        if (user == null || !matches) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants incorrects.");
        }
        return tokens.issue(user);
    }

    @GetMapping("/me")
    AuthDtos.UserResponse me(@AuthenticationPrincipal Jwt jwt) {
        return AuthDtos.UserResponse.of(currentUser(jwt));
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    void changePassword(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody AuthDtos.PasswordChangeRequest request) {
        AdminUser user = currentUser(jwt);
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Le mot de passe actuel est incorrect.");
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        users.save(user);
    }

    private AdminUser currentUser(Jwt jwt) {
        return users.findById(Long.valueOf(jwt.getSubject()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Compte introuvable."));
    }
}
