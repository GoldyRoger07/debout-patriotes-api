package com.deboutpatriotes.api.gallery;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deboutpatriotes.api.gallery.GalleryDtos.AlbumResponse;
import com.deboutpatriotes.api.gallery.GalleryDtos.AlbumSummary;

/** Lecture publique de la galerie : seuls les albums publiés sont exposés. */
@RestController
@RequestMapping("/api/albums")
class GalleryController {

    private final AlbumService albums;

    GalleryController(AlbumService albums) {
        this.albums = albums;
    }

    @GetMapping
    List<AlbumSummary> list() {
        return albums.listPublished();
    }

    @GetMapping("/{slug}")
    AlbumResponse get(@PathVariable String slug) {
        return albums.getPublished(slug);
    }
}
