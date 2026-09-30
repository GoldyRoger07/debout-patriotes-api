package com.deboutpatriotes.api.gallery;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.deboutpatriotes.api.media.ImageReferences;

/** Photos et vidéos utilisées par les albums de la galerie. */
@Component
class AlbumMediaReferences implements ImageReferences {

    private final AlbumRepository albums;

    AlbumMediaReferences(AlbumRepository albums) {
        this.albums = albums;
    }

    @Override
    public String usedBy(String fileId, String url) {
        List<String> titles = albums.findTitlesUsingFile(fileId);
        return titles.isEmpty() ? null : "l'album « " + titles.get(0) + " »";
    }

    @Override
    public Set<String> referenced(Collection<String> fileIds) {
        return fileIds.isEmpty() ? Set.of() : Set.copyOf(albums.findFileIdsIn(fileIds));
    }
}
