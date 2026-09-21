package com.deboutpatriotes.api.candidate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.BatchSize;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
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

/** Fiche d'un candidat, affichée sur `/candidats/<slug>`. */
@Entity
@Table(name = "candidate")
@Getter
@Setter
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String subtitle;

    @Column(name = "photo_url")
    private String photoUrl;

    /** Identifiant ImageKit de la photo, pour la supprimer quand elle est remplacée. */
    @Column(name = "photo_file_id")
    private String photoFileId;

    /** Poste brigué. */
    @Column(nullable = false)
    private String position;

    @Column(nullable = false)
    private String constituency;

    @Column(nullable = false)
    private String party;

    @Column(nullable = false)
    private String profession;

    @Column(nullable = false)
    private String birthplace;

    private String quote;

    private String email;
    private String facebook;
    private String x;
    private String instagram;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(nullable = false)
    private boolean published = true;

    @ElementCollection
    @CollectionTable(name = "candidate_bio", joinColumns = @JoinColumn(name = "candidate_id"))
    @OrderColumn(name = "sort_order")
    @Column(name = "paragraph", nullable = false, columnDefinition = "TEXT")
    @BatchSize(size = 50)
    private List<String> bio = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "candidate_priority", joinColumns = @JoinColumn(name = "candidate_id"))
    @OrderColumn(name = "sort_order")
    @BatchSize(size = 50)
    private List<Priority> priorities = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "candidate_career", joinColumns = @JoinColumn(name = "candidate_id"))
    @OrderColumn(name = "sort_order")
    @BatchSize(size = 50)
    private List<CareerEntry> career = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "candidate_education", joinColumns = @JoinColumn(name = "candidate_id"))
    @OrderColumn(name = "sort_order")
    @Column(name = "entry", nullable = false)
    @BatchSize(size = 50)
    private List<String> education = new ArrayList<>();

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

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Priority {
        @Column(nullable = false)
        private String title;
        @Column(nullable = false, columnDefinition = "TEXT")
        private String description;
        private String icon;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CareerEntry {
        @Column(nullable = false)
        private String period;
        @Column(nullable = false)
        private String title;
        @Column(columnDefinition = "TEXT")
        private String description;
    }
}
