package com.deboutpatriotes.api.event;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deboutpatriotes.api.event.EventDtos.EventResponse;

/** Lecture publique de l'agenda : seuls les événements publiés sont exposés. */
@RestController
@RequestMapping("/api/events")
class EventController {

    private final EventService events;

    EventController(EventService events) {
        this.events = events;
    }

    @GetMapping
    List<EventResponse> list() {
        return events.listPublished();
    }
}
