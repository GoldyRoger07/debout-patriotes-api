package com.deboutpatriotes.api.candidate;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deboutpatriotes.api.candidate.CandidateDtos.CandidateRequest;
import com.deboutpatriotes.api.candidate.CandidateDtos.CandidateResponse;
import com.deboutpatriotes.api.candidate.CandidateDtos.CareerDto;
import com.deboutpatriotes.api.candidate.CandidateDtos.ContactDto;
import com.deboutpatriotes.api.candidate.CandidateDtos.PriorityDto;
import com.deboutpatriotes.api.common.BadRequestException;
import com.deboutpatriotes.api.common.ConflictException;
import com.deboutpatriotes.api.common.NotFoundException;
import com.deboutpatriotes.api.common.Slugs;
import com.deboutpatriotes.api.media.ImageKitService;

@Service
@Transactional(readOnly = true)
public class CandidateService {

    private final CandidateRepository candidates;
    private final ImageKitService imageKit;

    CandidateService(CandidateRepository candidates, ImageKitService imageKit) {
        this.candidates = candidates;
        this.imageKit = imageKit;
    }

    public List<CandidateResponse> listPublished() {
        return candidates.findAllByPublishedTrueOrderByDisplayOrderAscIdAsc().stream().map(this::toResponse).toList();
    }

    public CandidateResponse getPublished(String slug) {
        return candidates.findBySlugAndPublishedTrue(slug).map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Candidat introuvable."));
    }

    public List<CandidateResponse> listAll() {
        return candidates.findAllByOrderByDisplayOrderAscIdAsc().stream().map(this::toResponse).toList();
    }

    public CandidateResponse get(Long id) {
        return toResponse(find(id));
    }

    @Transactional
    public CandidateResponse create(CandidateRequest request) {
        Candidate candidate = new Candidate();
        candidate.setDisplayOrder(candidates.maxDisplayOrder() + 1);
        apply(candidate, request);
        return toResponse(candidates.save(candidate));
    }

    @Transactional
    public CandidateResponse update(Long id, CandidateRequest request) {
        Candidate candidate = find(id);
        String previousPhoto = candidate.getPhotoFileId();
        String previousCover = candidate.getCoverFileId();
        apply(candidate, request);
        if (previousPhoto != null && !Objects.equals(previousPhoto, candidate.getPhotoFileId())) {
            imageKit.deleteAfterCommit(previousPhoto);
        }
        if (previousCover != null && !Objects.equals(previousCover, candidate.getCoverFileId())) {
            imageKit.deleteAfterCommit(previousCover);
        }
        return toResponse(candidates.save(candidate));
    }

    @Transactional
    public void delete(Long id) {
        Candidate candidate = find(id);
        candidates.delete(candidate);
        imageKit.deleteAfterCommit(candidate.getPhotoFileId());
        imageKit.deleteAfterCommit(candidate.getCoverFileId());
    }

    /** Réordonne les candidats selon la liste d'identifiants fournie (ordre d'affichage du site). */
    @Transactional
    public List<CandidateResponse> reorder(List<Long> ids) {
        Map<Long, Candidate> byId = candidates.findAll().stream()
                .collect(Collectors.toMap(Candidate::getId, Function.identity()));
        if (ids.size() != byId.size() || !byId.keySet().containsAll(ids)) {
            throw new BadRequestException("La liste doit contenir chaque candidat une et une seule fois.");
        }
        for (int i = 0; i < ids.size(); i++) {
            byId.get(ids.get(i)).setDisplayOrder(i + 1);
        }
        return listAll();
    }

    private Candidate find(Long id) {
        return candidates.findById(id).orElseThrow(() -> new NotFoundException("Candidat introuvable."));
    }

    private void apply(Candidate c, CandidateRequest r) {
        String slug = Slugs.resolve(r.slug(), r.name());
        boolean taken = c.getId() == null ? candidates.existsBySlug(slug) : candidates.existsBySlugAndIdNot(slug, c.getId());
        if (taken) {
            throw new ConflictException("L'adresse « " + slug + " » est déjà utilisée par un autre candidat.");
        }
        c.setSlug(slug);
        c.setName(r.name().trim());
        c.setSubtitle(blankToNull(r.subtitle()));
        c.setPhotoUrl(blankToNull(r.photo()));
        c.setPhotoFileId(c.getPhotoUrl() == null ? null : blankToNull(r.photoFileId()));
        c.setCoverUrl(blankToNull(r.cover()));
        c.setCoverFileId(c.getCoverUrl() == null ? null : blankToNull(r.coverFileId()));
        c.setPosition(blankToNull(r.position()));
        c.setConstituency(blankToNull(r.constituency()));
        c.setParty(blankToNull(r.party()));
        c.setBirthplace(blankToNull(r.birthplace()));
        c.setQuote(blankToNull(r.quote()));
        if (r.published() != null) {
            c.setPublished(r.published());
        }

        ContactDto contact = r.contact();
        c.setEmail(contact == null ? null : blankToNull(contact.email()));
        c.setFacebook(contact == null ? null : blankToNull(contact.facebook()));
        c.setX(contact == null ? null : blankToNull(contact.x()));
        c.setInstagram(contact == null ? null : blankToNull(contact.instagram()));

        // Les collections sont vidées puis remplies (et non réaffectées) pour que Hibernate suive les changements.
        c.getProfessions().clear();
        c.getProfessions().addAll(orEmpty(r.professions()).stream().map(String::trim).toList());
        c.getBio().clear();
        c.getBio().addAll(orEmpty(r.bio()).stream().map(String::trim).toList());
        c.getPriorities().clear();
        c.getPriorities().addAll(orEmpty(r.priorities()).stream()
                .map(p -> new Candidate.Priority(p.title().trim(), p.desc().trim(), blankToNull(p.icon())))
                .toList());
        c.getCareer().clear();
        c.getCareer().addAll(orEmpty(r.career()).stream()
                .map(e -> new Candidate.CareerEntry(e.period().trim(), e.title().trim(), blankToNull(e.desc())))
                .toList());
        c.getEducation().clear();
        c.getEducation().addAll(orEmpty(r.education()).stream().map(String::trim).toList());
    }

    private CandidateResponse toResponse(Candidate c) {
        boolean hasContact = c.getEmail() != null || c.getFacebook() != null || c.getX() != null || c.getInstagram() != null;
        return new CandidateResponse(
                c.getId(), c.getSlug(), c.getName(), c.getSubtitle(),
                c.getPhotoUrl(), c.getPhotoFileId(), c.getCoverUrl(), c.getCoverFileId(),
                c.getPosition(), c.getConstituency(), c.getParty(), List.copyOf(c.getProfessions()),
                c.getBirthplace(), c.getQuote(),
                List.copyOf(c.getBio()),
                c.getPriorities().stream().map(p -> new PriorityDto(p.getTitle(), p.getDescription(), p.getIcon())).toList(),
                c.getCareer().stream().map(e -> new CareerDto(e.getPeriod(), e.getTitle(), e.getDescription())).toList(),
                List.copyOf(c.getEducation()),
                hasContact ? new ContactDto(c.getEmail(), c.getFacebook(), c.getX(), c.getInstagram()) : null,
                c.getDisplayOrder(), c.isPublished(), c.getUpdatedAt());
    }

    private static <T> List<T> orEmpty(List<T> list) {
        return list == null ? List.of() : list;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
