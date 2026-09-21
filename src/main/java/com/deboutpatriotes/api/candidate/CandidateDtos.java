package com.deboutpatriotes.api.candidate;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Formes JSON des candidats. Les noms de champs suivent l'interface `Candidate` du site Angular
 * (`photo`, `priorities[].desc`, `career[].desc`, `contact`) pour que le front les consomme telles quelles.
 */
public final class CandidateDtos {

    private CandidateDtos() {
    }

    public record PriorityDto(
            @NotBlank @Size(max = 160) String title,
            @NotBlank String desc,
            @Size(max = 60) String icon) {
    }

    public record CareerDto(
            @NotBlank @Size(max = 60) String period,
            @NotBlank @Size(max = 255) String title,
            String desc) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ContactDto(
            @Email @Size(max = 190) String email,
            @Size(max = 255) String facebook,
            @Size(max = 255) String x,
            @Size(max = 255) String instagram) {
    }

    public record CandidateResponse(
            Long id,
            String slug,
            String name,
            String subtitle,
            String photo,
            String photoFileId,
            String position,
            String constituency,
            String party,
            String profession,
            String birthplace,
            String quote,
            List<String> bio,
            List<PriorityDto> priorities,
            List<CareerDto> career,
            List<String> education,
            ContactDto contact,
            int displayOrder,
            boolean published,
            Instant updatedAt) {
    }

    public record CandidateRequest(
            @Size(max = 190) String slug,
            @NotBlank @Size(max = 160) String name,
            @NotBlank @Size(max = 255) String subtitle,
            @Size(max = 500) String photo,
            @Size(max = 100) String photoFileId,
            @NotBlank @Size(max = 160) String position,
            @NotBlank @Size(max = 160) String constituency,
            @NotBlank @Size(max = 160) String party,
            @NotBlank @Size(max = 160) String profession,
            @NotBlank @Size(max = 160) String birthplace,
            @Size(max = 500) String quote,
            @NotEmpty(message = "Au moins un paragraphe de biographie") List<@NotBlank String> bio,
            List<@Valid PriorityDto> priorities,
            List<@Valid CareerDto> career,
            List<@NotBlank @Size(max = 500) String> education,
            @Valid ContactDto contact,
            Boolean published) {
    }

    public record OrderRequest(@NotNull List<Long> ids) {
    }
}
