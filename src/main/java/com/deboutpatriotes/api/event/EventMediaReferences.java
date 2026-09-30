package com.deboutpatriotes.api.event;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.deboutpatriotes.api.media.ImageReferences;

/** Fichiers utilisés par l'agenda : couverture et vidéo de chaque événement. */
@Component
class EventMediaReferences implements ImageReferences {

    private final EventRepository events;

    EventMediaReferences(EventRepository events) {
        this.events = events;
    }

    @Override
    public String usedBy(String fileId, String url) {
        List<String> titles = events.findTitlesUsingFile(fileId);
        return titles.isEmpty() ? null : "l'événement « " + titles.get(0) + " »";
    }

    @Override
    public Set<String> referenced(Collection<String> fileIds) {
        if (fileIds.isEmpty()) {
            return Set.of();
        }
        Set<String> used = new HashSet<>(events.findCoverFileIdsIn(fileIds));
        used.addAll(events.findVideoFileIdsIn(fileIds));
        return used;
    }
}
