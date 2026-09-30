package com.deboutpatriotes.api.gallery;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import com.deboutpatriotes.api.gallery.Album.MediaType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Formes JSON de la galerie (voir `gallery.model.ts` côté Angular). */
public final class GalleryDtos {

    private GalleryDtos() {
    }

    public record ItemDto(
            @NotNull MediaType type,
            @NotBlank @Size(max = 500) String url,
            @Size(max = 100) String fileId,
            @Size(max = 255) String caption) {

        static ItemDto of(Album.Item i) {
            return new ItemDto(i.getType(), i.getUrl(), i.getFileId(), i.getCaption());
        }
    }

    /** Album sans ses éléments : grille de `/galerie` et liste du back-office. */
    public record AlbumSummary(
            Long id,
            String slug,
            String title,
            LocalDate takenOn,
            /** Premier élément de l'album, photo ou vidéo. */
            ItemDto cover,
            int photoCount,
            int videoCount,
            boolean published,
            Instant updatedAt) {

        static AlbumSummary of(Album a) {
            return new AlbumSummary(a.getId(), a.getSlug(), a.getTitle(), a.getTakenOn(),
                    a.getItems().isEmpty() ? null : ItemDto.of(a.getItems().get(0)),
                    count(a, MediaType.IMAGE), count(a, MediaType.VIDEO), a.isPublished(), a.getUpdatedAt());
        }

        private static int count(Album a, MediaType type) {
            return (int) a.getItems().stream().filter(i -> i.getType() == type).count();
        }
    }

    public record AlbumResponse(
            Long id,
            String slug,
            String title,
            String description,
            LocalDate takenOn,
            List<ItemDto> items,
            boolean published,
            Instant updatedAt) {

        static AlbumResponse of(Album a) {
            return new AlbumResponse(a.getId(), a.getSlug(), a.getTitle(), a.getDescription(), a.getTakenOn(),
                    a.getItems().stream().map(ItemDto::of).toList(), a.isPublished(), a.getUpdatedAt());
        }
    }

    public record AlbumRequest(
            @Size(max = 190) String slug,
            @NotBlank @Size(max = 255) String title,
            String description,
            LocalDate takenOn,
            @Size(max = 500) List<@Valid @NotNull ItemDto> items,
            Boolean published) {
    }
}
