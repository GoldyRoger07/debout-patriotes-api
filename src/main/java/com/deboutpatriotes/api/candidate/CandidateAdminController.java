package com.deboutpatriotes.api.candidate;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.deboutpatriotes.api.candidate.CandidateDtos.CandidateRequest;
import com.deboutpatriotes.api.candidate.CandidateDtos.CandidateResponse;
import com.deboutpatriotes.api.candidate.CandidateDtos.OrderRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/candidates")
class CandidateAdminController {

    private final CandidateService service;

    CandidateAdminController(CandidateService service) {
        this.service = service;
    }

    @GetMapping
    List<CandidateResponse> list() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    CandidateResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CandidateResponse create(@Valid @RequestBody CandidateRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    CandidateResponse update(@PathVariable Long id, @Valid @RequestBody CandidateRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PutMapping("/order")
    List<CandidateResponse> reorder(@Valid @RequestBody OrderRequest request) {
        return service.reorder(request.ids());
    }
}
