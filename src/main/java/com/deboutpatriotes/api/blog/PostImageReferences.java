package com.deboutpatriotes.api.blog;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.deboutpatriotes.api.media.ImageReferences;

/** Fichiers utilisés par les articles : couverture, vidéo et images insérées dans le corps. */
@Component
class PostImageReferences implements ImageReferences {

    private final PostRepository posts;

    PostImageReferences(PostRepository posts) {
        this.posts = posts;
    }

    @Override
    public String usedBy(String fileId, String url) {
        List<String> titles = posts.findTitlesUsingImage(fileId, url);
        return titles.isEmpty() ? null : "l'article « " + titles.get(0) + " »";
    }

    @Override
    public Set<String> referenced(Collection<String> fileIds) {
        if (fileIds.isEmpty()) {
            return Set.of();
        }
        Set<String> used = new HashSet<>(posts.findCoverFileIdsIn(fileIds));
        used.addAll(posts.findVideoFileIdsIn(fileIds));
        return used;
    }
}
