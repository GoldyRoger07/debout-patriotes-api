package com.deboutpatriotes.api.candidate;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.deboutpatriotes.api.media.ImageReferences;

/** Images utilisées par les fiches candidat : la photo de portrait. */
@Component
class CandidateImageReferences implements ImageReferences {

    private final CandidateRepository candidates;

    CandidateImageReferences(CandidateRepository candidates) {
        this.candidates = candidates;
    }

    /** La fiche candidat n'a pas de texte libre illustré : seul l'identifiant de la photo compte. */
    @Override
    public String usedBy(String fileId, String url) {
        List<String> names = candidates.findNamesUsingImage(fileId);
        return names.isEmpty() ? null : "la fiche de " + names.get(0);
    }

    @Override
    public Set<String> referenced(Collection<String> fileIds) {
        return fileIds.isEmpty() ? Set.of() : Set.copyOf(candidates.findPhotoFileIdsIn(fileIds));
    }
}
