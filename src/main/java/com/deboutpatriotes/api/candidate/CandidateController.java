package com.deboutpatriotes.api.candidate;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deboutpatriotes.api.candidate.CandidateDtos.CandidateResponse;
import com.deboutpatriotes.api.candidate.CandidateDtos.CardFormats;

/** Lecture publique des candidats publiés, dans l'ordre d'affichage choisi dans le back-office. */
@RestController
@RequestMapping("/api/candidates")
class CandidateController {

    private final CandidateService service;
    private final CandidateCardFormatService cardFormats;

    CandidateController(CandidateService service, CandidateCardFormatService cardFormats) {
        this.service = service;
        this.cardFormats = cardFormats;
    }

    @GetMapping
    List<CandidateResponse> list() {
        return service.listPublished();
    }

    /** Proportion des photos des cartes, par emplacement du site. */
    @GetMapping("/card-formats")
    CardFormats cardFormats() {
        return cardFormats.get();
    }

    @GetMapping("/{slug}")
    CandidateResponse get(@PathVariable String slug) {
        return service.getPublished(slug);
    }
}
