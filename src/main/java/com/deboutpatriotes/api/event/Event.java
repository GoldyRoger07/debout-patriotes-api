package com.deboutpatriotes.api.event;

import java.time.Instant;

import com.deboutpatriotes.api.media.ImageFocus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Événement de l'agenda, affiché sur `/evenements` : à venir tant que sa date n'est pas passée,
 * puis dans les événements passés — où sa vidéo sert de rediffusion.
 */
@Entity
@Table(name = "agenda_event")
@Getter
@Setter
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String title;

    /** Nature de l'événement : assemblée, conférence de presse, meeting… */
    @Column(length = 80)
    private String kind;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    private String place;

    @Column(length = 160)
    private String city;

    @Column(name = "cover_url")
    private String coverUrl;

    @Column(name = "cover_file_id")
    private String coverFileId;

    @Enumerated(EnumType.STRING)
    @Column(name = "cover_focus", length = 20)
    private ImageFocus coverFocus;

    /** Vidéo ImageKit : annonce avant l'événement, rediffusion après. */
    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "video_file_id")
    private String videoFileId;

    @Column(nullable = false)
    private boolean published = true;

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
}
