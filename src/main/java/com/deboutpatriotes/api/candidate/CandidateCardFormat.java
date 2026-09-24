package com.deboutpatriotes.api.candidate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Proportion des photos des cartes candidats pour un emplacement du site (`home`, `list`). */
@Entity
@Table(name = "candidate_card_format")
@Getter
@Setter
@NoArgsConstructor
public class CandidateCardFormat {

    @Id
    @Column(length = 20)
    private String placement;

    @Column(name = "ratio_width", nullable = false)
    private int ratioWidth;

    @Column(name = "ratio_height", nullable = false)
    private int ratioHeight;

    CandidateCardFormat(String placement) {
        this.placement = placement;
    }
}
