package com.deboutpatriotes.api.blog;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class BlogDtos {

    private BlogDtos() {
    }

    public record CategoryResponse(Long id, String name, String slug, int displayOrder, Long postCount) {
        static CategoryResponse of(Category c) {
            return of(c, null);
        }

        static CategoryResponse of(Category c, Long postCount) {
            return c == null ? null : new CategoryResponse(c.getId(), c.getName(), c.getSlug(), c.getDisplayOrder(), postCount);
        }
    }

    public record CategoryRequest(
            @NotBlank @Size(max = 80) String name,
            @Size(max = 100) String slug,
            Integer displayOrder) {
    }

    /** Article sans son corps : listes du site et du back-office. */
    public record PostSummary(
            Long id,
            String slug,
            String title,
            String excerpt,
            String cover,
            CategoryResponse category,
            PostStatus status,
            Instant publishedAt,
            Instant updatedAt) {
        static PostSummary of(Post p) {
            return new PostSummary(p.getId(), p.getSlug(), p.getTitle(), p.getExcerpt(), p.getCoverUrl(),
                    CategoryResponse.of(p.getCategory()), p.getStatus(), p.getPublishedAt(), p.getUpdatedAt());
        }
    }

    public record PostResponse(
            Long id,
            String slug,
            String title,
            String excerpt,
            /** Corps de l'article en Markdown. */
            String content,
            String cover,
            String coverFileId,
            CategoryResponse category,
            PostStatus status,
            Instant publishedAt,
            Instant createdAt,
            Instant updatedAt) {
        static PostResponse of(Post p) {
            return new PostResponse(p.getId(), p.getSlug(), p.getTitle(), p.getExcerpt(), p.getContent(),
                    p.getCoverUrl(), p.getCoverFileId(), CategoryResponse.of(p.getCategory()), p.getStatus(),
                    p.getPublishedAt(), p.getCreatedAt(), p.getUpdatedAt());
        }
    }

    public record PostRequest(
            @Size(max = 190) String slug,
            @NotBlank @Size(max = 255) String title,
            @NotBlank @Size(max = 600) String excerpt,
            @NotBlank String content,
            @Size(max = 500) String cover,
            @Size(max = 100) String coverFileId,
            Long categoryId,
            @NotNull PostStatus status,
            /** Date de publication ; vide = maintenant au moment de publier. Une date future programme l'article. */
            Instant publishedAt) {
    }
}
