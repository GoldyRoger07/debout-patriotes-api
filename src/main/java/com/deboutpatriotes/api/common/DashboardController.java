package com.deboutpatriotes.api.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deboutpatriotes.api.blog.PostRepository;
import com.deboutpatriotes.api.blog.PostStatus;
import com.deboutpatriotes.api.candidate.CandidateRepository;
import com.deboutpatriotes.api.media.ImageKitProperties;

/** Chiffres de la page d'accueil du back-office. */
@RestController
class DashboardController {

    private final PostRepository posts;
    private final CandidateRepository candidates;
    private final ImageKitProperties imageKit;

    DashboardController(PostRepository posts, CandidateRepository candidates, ImageKitProperties imageKit) {
        this.posts = posts;
        this.candidates = candidates;
        this.imageKit = imageKit;
    }

    @GetMapping("/api/admin/dashboard")
    Dashboard dashboard() {
        return new Dashboard(posts.countByStatus(PostStatus.PUBLISHED), posts.countByStatus(PostStatus.DRAFT),
                candidates.countByPublishedTrue(), candidates.count(), imageKit.configured());
    }

    record Dashboard(long publishedPosts, long draftPosts, long publishedCandidates, long totalCandidates,
            boolean imageKitConfigured) {
    }
}
