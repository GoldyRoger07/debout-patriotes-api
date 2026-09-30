package com.deboutpatriotes.api.gallery;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.BatchSize;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Album de la galerie, affiché sur `/galerie/<slug>` : photos et vidéos dans l'ordre choisi. */
@Entity
@Table(name = "album")
@Getter
@Setter
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    /** Date des prises de vue (l'événement couvert), qui ordonne la galerie. */
    @Column(name = "taken_on")
    private LocalDate takenOn;

    @Column(nullable = false)
    private boolean published = true;

    /** Le premier élément sert de couverture à l'album. */
    @ElementCollection
    @CollectionTable(name = "album_item", joinColumns = @JoinColumn(name = "album_id"))
    @OrderColumn(name = "sort_order")
    @BatchSize(size = 50)
    private List<Item> items = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    /** Nature d'un élément ; en minuscules dans le JSON, comme le type `MediaType` du front. */
    public enum MediaType {
        IMAGE, VIDEO;

        @JsonValue
        public String jsonValue() {
            return name().toLowerCase();
        }

        @JsonCreator
        public static MediaType fromJson(String value) {
            return value == null || value.isBlank() ? null : valueOf(value.trim().toUpperCase());
        }
    }

    /** Une photo ou une vidéo hébergée sur ImageKit. */
    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        @Enumerated(EnumType.STRING)
        @Column(name = "media_type", nullable = false, length = 10)
        private MediaType type;
        @Column(nullable = false, length = 500)
        private String url;
        @Column(name = "file_id", length = 100)
        private String fileId;
        private String caption;
    }
}
