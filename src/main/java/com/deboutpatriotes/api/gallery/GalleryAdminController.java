package com.deboutpatriotes.api.gallery;

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

import com.deboutpatriotes.api.gallery.GalleryDtos.AlbumRequest;
import com.deboutpatriotes.api.gallery.GalleryDtos.AlbumResponse;
import com.deboutpatriotes.api.gallery.GalleryDtos.AlbumSummary;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/albums")
class GalleryAdminController {

    private final AlbumService albums;

    GalleryAdminController(AlbumService albums) {
        this.albums = albums;
    }

    @GetMapping
    List<AlbumSummary> list() {
        return albums.listAll();
    }

    @GetMapping("/{id}")
    AlbumResponse get(@PathVariable Long id) {
        return albums.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    AlbumResponse create(@Valid @RequestBody AlbumRequest request) {
        return albums.create(request);
    }

    @PutMapping("/{id}")
    AlbumResponse update(@PathVariable Long id, @Valid @RequestBody AlbumRequest request) {
        return albums.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Long id) {
        albums.delete(id);
    }
}
