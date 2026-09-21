package com.deboutpatriotes.api.candidate;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deboutpatriotes.api.candidate.CandidateDtos.CandidateResponse;

/** Lecture publique des candidats publiés, dans l'ordre d'affichage choisi dans le back-office. */
@RestController
@RequestMapping("/api/candidates")
class CandidateController {

    private final CandidateService service;

    CandidateController(CandidateService service) {
        this.service = service;
    }

    @GetMapping
    List<CandidateResponse> list() {
        return service.listPublished();
    }

    @GetMapping("/{slug}")
    CandidateResponse get(@PathVariable String slug) {
        return service.getPublished(slug);
    }
}
