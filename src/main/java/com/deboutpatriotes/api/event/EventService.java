package com.deboutpatriotes.api.event;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deboutpatriotes.api.common.ConflictException;
import com.deboutpatriotes.api.common.NotFoundException;
import com.deboutpatriotes.api.common.Slugs;
import com.deboutpatriotes.api.event.EventDtos.EventRequest;
import com.deboutpatriotes.api.event.EventDtos.EventResponse;
import com.deboutpatriotes.api.media.ImageKitService;

@Service
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository events;
    private final ImageKitService imageKit;

    EventService(EventRepository events, ImageKitService imageKit) {
        this.events = events;
        this.imageKit = imageKit;
    }

    /** Événements publiés, du plus lointain au plus ancien ; le site sépare « à venir » et « passés ». */
    public List<EventResponse> listPublished() {
        return events.findAllByPublishedTrueOrderByStartsAtDesc().stream().map(EventResponse::of).toList();
    }

    public List<EventResponse> listAll() {
        return events.findAllByOrderByStartsAtDesc().stream().map(EventResponse::of).toList();
    }

    public EventResponse get(Long id) {
        return EventResponse.of(find(id));
    }

    @Transactional
    public EventResponse create(EventRequest request) {
        Event event = new Event();
        apply(event, request);
        return EventResponse.of(events.save(event));
    }

    @Transactional
    public EventResponse update(Long id, EventRequest request) {
        Event event = find(id);
        String previousCover = event.getCoverFileId();
        String previousVideo = event.getVideoFileId();
        apply(event, request);
        if (previousCover != null && !Objects.equals(previousCover, event.getCoverFileId())) {
            imageKit.deleteAfterCommit(previousCover);
        }
        if (previousVideo != null && !Objects.equals(previousVideo, event.getVideoFileId())) {
            imageKit.deleteAfterCommit(previousVideo);
        }
        return EventResponse.of(events.save(event));
    }

    @Transactional
    public void delete(Long id) {
        Event event = find(id);
        events.delete(event);
        imageKit.deleteAfterCommit(event.getCoverFileId());
        imageKit.deleteAfterCommit(event.getVideoFileId());
    }

    private Event find(Long id) {
        return events.findById(id).orElseThrow(() -> new NotFoundException("Événement introuvable."));
    }

    private void apply(Event e, EventRequest r) {
        String slug = Slugs.resolve(r.slug(), r.title());
        boolean taken = e.getId() == null ? events.existsBySlug(slug) : events.existsBySlugAndIdNot(slug, e.getId());
        if (taken) {
            throw new ConflictException("L'adresse « " + slug + " » est déjà utilisée par un autre événement.");
        }
        e.setSlug(slug);
        e.setTitle(r.title().trim());
        e.setKind(blankToNull(r.kind()));
        e.setDescription(blankToNull(r.description()));
        e.setStartsAt(r.startsAt());
        e.setPlace(blankToNull(r.place()));
        e.setCity(blankToNull(r.city()));
        e.setCoverUrl(blankToNull(r.cover()));
        e.setCoverFileId(e.getCoverUrl() == null ? null : blankToNull(r.coverFileId()));
        e.setCoverFocus(e.getCoverUrl() == null ? null : r.coverFocus());
        e.setVideoUrl(blankToNull(r.video()));
        e.setVideoFileId(e.getVideoUrl() == null ? null : blankToNull(r.videoFileId()));
        if (r.published() != null) {
            e.setPublished(r.published());
        }
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
