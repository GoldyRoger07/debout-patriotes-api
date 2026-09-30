package com.deboutpatriotes.api.event;

import java.time.Instant;

import com.deboutpatriotes.api.media.ImageFocus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Formes JSON de l'agenda (voir `event.model.ts` côté Angular). */
public final class EventDtos {

    private EventDtos() {
    }

    public record EventResponse(
            Long id,
            String slug,
            String title,
            String kind,
            String description,
            Instant startsAt,
            String place,
            String city,
            String cover,
            String coverFileId,
            ImageFocus coverFocus,
            String video,
            String videoFileId,
            boolean published,
            Instant updatedAt) {

        static EventResponse of(Event e) {
            return new EventResponse(e.getId(), e.getSlug(), e.getTitle(), e.getKind(), e.getDescription(),
                    e.getStartsAt(), e.getPlace(), e.getCity(), e.getCoverUrl(), e.getCoverFileId(),
                    e.getCoverFocus(), e.getVideoUrl(), e.getVideoFileId(), e.isPublished(), e.getUpdatedAt());
        }
    }

    public record EventRequest(
            @Size(max = 190) String slug,
            @NotBlank @Size(max = 255) String title,
            @Size(max = 80) String kind,
            String description,
            @NotNull Instant startsAt,
            @Size(max = 255) String place,
            @Size(max = 160) String city,
            @Size(max = 500) String cover,
            @Size(max = 100) String coverFileId,
            ImageFocus coverFocus,
            @Size(max = 500) String video,
            @Size(max = 100) String videoFileId,
            Boolean published) {
    }
}
