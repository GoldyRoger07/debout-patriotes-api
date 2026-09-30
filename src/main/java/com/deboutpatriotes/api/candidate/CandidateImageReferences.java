package com.deboutpatriotes.api.candidate;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.deboutpatriotes.api.media.ImageReferences;

/** Fichiers utilisés par les fiches candidat : le portrait, la photo de couverture et la vidéo. */
@Component
class CandidateImageReferences implements ImageReferences {

    private final CandidateRepository candidates;

    CandidateImageReferences(CandidateRepository candidates) {
        this.candidates = candidates;
    }

    /** La fiche candidat n'a pas de texte libre illustré : seuls les identifiants des fichiers comptent. */
    @Override
    public String usedBy(String fileId, String url) {
        List<String> names = candidates.findNamesUsingImage(fileId);
        return names.isEmpty() ? null : "la fiche de " + names.get(0);
    }

    @Override
    public Set<String> referenced(Collection<String> fileIds) {
        if (fileIds.isEmpty()) {
            return Set.of();
        }
        Set<String> used = new HashSet<>(candidates.findPhotoFileIdsIn(fileIds));
        used.addAll(candidates.findCoverFileIdsIn(fileIds));
        used.addAll(candidates.findVideoFileIdsIn(fileIds));
        return used;
    }
}
