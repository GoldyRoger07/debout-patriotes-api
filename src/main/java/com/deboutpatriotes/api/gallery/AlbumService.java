package com.deboutpatriotes.api.gallery;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deboutpatriotes.api.common.ConflictException;
import com.deboutpatriotes.api.common.NotFoundException;
import com.deboutpatriotes.api.common.Slugs;
import com.deboutpatriotes.api.gallery.GalleryDtos.AlbumRequest;
import com.deboutpatriotes.api.gallery.GalleryDtos.AlbumResponse;
import com.deboutpatriotes.api.gallery.GalleryDtos.AlbumSummary;
import com.deboutpatriotes.api.media.ImageKitService;

@Service
@Transactional(readOnly = true)
public class AlbumService {

    private final AlbumRepository albums;
    private final ImageKitService imageKit;

    AlbumService(AlbumRepository albums, ImageKitService imageKit) {
        this.albums = albums;
        this.imageKit = imageKit;
    }

    public List<AlbumSummary> listPublished() {
        return albums.findPublished().stream().map(AlbumSummary::of).toList();
    }

    public AlbumResponse getPublished(String slug) {
        return albums.findBySlugAndPublishedTrue(slug).map(AlbumResponse::of)
                .orElseThrow(() -> new NotFoundException("Album introuvable."));
    }

    public List<AlbumSummary> listAll() {
        return albums.findAllOrdered().stream().map(AlbumSummary::of).toList();
    }

    public AlbumResponse get(Long id) {
        return AlbumResponse.of(find(id));
    }

    @Transactional
    public AlbumResponse create(AlbumRequest request) {
        Album album = new Album();
        apply(album, request);
        return AlbumResponse.of(albums.save(album));
    }

    /** Les photos et vidéos retirées de l'album sont supprimées d'ImageKit après l'enregistrement. */
    @Transactional
    public AlbumResponse update(Long id, AlbumRequest request) {
        Album album = find(id);
        Set<String> before = fileIds(album);
        apply(album, request);
        before.removeAll(fileIds(album));
        before.forEach(imageKit::deleteAfterCommit);
        return AlbumResponse.of(albums.save(album));
    }

    @Transactional
    public void delete(Long id) {
        Album album = find(id);
        Set<String> files = fileIds(album);
        albums.delete(album);
        files.forEach(imageKit::deleteAfterCommit);
    }

    private Album find(Long id) {
        return albums.findById(id).orElseThrow(() -> new NotFoundException("Album introuvable."));
    }

    private void apply(Album album, AlbumRequest r) {
        String slug = Slugs.resolve(r.slug(), r.title());
        boolean taken = album.getId() == null ? albums.existsBySlug(slug) : albums.existsBySlugAndIdNot(slug, album.getId());
        if (taken) {
            throw new ConflictException("L'adresse « " + slug + " » est déjà utilisée par un autre album.");
        }
        album.setSlug(slug);
        album.setTitle(r.title().trim());
        album.setDescription(blankToNull(r.description()));
        album.setTakenOn(r.takenOn());
        if (r.published() != null) {
            album.setPublished(r.published());
        }
        // Vidée puis remplie (et non réaffectée) pour que Hibernate suive les changements.
        album.getItems().clear();
        album.getItems().addAll((r.items() == null ? List.<GalleryDtos.ItemDto>of() : r.items()).stream()
                .map(i -> new Album.Item(i.type(), i.url().trim(), blankToNull(i.fileId()), blankToNull(i.caption())))
                .toList());
    }

    private static Set<String> fileIds(Album album) {
        Set<String> ids = new HashSet<>();
        album.getItems().stream().map(Album.Item::getFileId).filter(Objects::nonNull).forEach(ids::add);
        return ids;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
