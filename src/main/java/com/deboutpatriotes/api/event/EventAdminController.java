package com.deboutpatriotes.api.event;

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

import com.deboutpatriotes.api.event.EventDtos.EventRequest;
import com.deboutpatriotes.api.event.EventDtos.EventResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/events")
class EventAdminController {

    private final EventService events;

    EventAdminController(EventService events) {
        this.events = events;
    }

    @GetMapping
    List<EventResponse> list() {
        return events.listAll();
    }

    @GetMapping("/{id}")
    EventResponse get(@PathVariable Long id) {
        return events.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    EventResponse create(@Valid @RequestBody EventRequest request) {
        return events.create(request);
    }

    @PutMapping("/{id}")
    EventResponse update(@PathVariable Long id, @Valid @RequestBody EventRequest request) {
        return events.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Long id) {
        events.delete(id);
    }
}
