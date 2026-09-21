package com.deboutpatriotes.api.auth;

import java.time.Instant;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

final class AuthDtos {

    private AuthDtos() {
    }

    record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {
    }

    record PasswordChangeRequest(
            @NotBlank String currentPassword,
            @NotBlank @Size(min = 8, message = "8 caractères minimum") String newPassword) {
    }

    record UserResponse(Long id, String email, String displayName) {
        static UserResponse of(AdminUser user) {
            return new UserResponse(user.getId(), user.getEmail(), user.getDisplayName());
        }
    }

    record TokenResponse(String token, Instant expiresAt, UserResponse user) {
    }
}
