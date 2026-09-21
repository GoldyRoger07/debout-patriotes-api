package com.deboutpatriotes.api.blog;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.deboutpatriotes.api.media.ImageReferences;

/** Images utilisées par les articles : image de couverture et images insérées dans le corps. */
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
        return fileIds.isEmpty() ? Set.of() : Set.copyOf(posts.findCoverFileIdsIn(fileIds));
    }
}
