package com.deboutpatriotes.api.blog;

import java.time.Instant;
import java.util.Objects;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deboutpatriotes.api.blog.BlogDtos.PostRequest;
import com.deboutpatriotes.api.blog.BlogDtos.PostResponse;
import com.deboutpatriotes.api.blog.BlogDtos.PostSummary;
import com.deboutpatriotes.api.common.ConflictException;
import com.deboutpatriotes.api.common.NotFoundException;
import com.deboutpatriotes.api.common.PageResponse;
import com.deboutpatriotes.api.common.Slugs;
import com.deboutpatriotes.api.media.ImageKitService;

@Service
@Transactional(readOnly = true)
public class PostService {

    private static final int MAX_PAGE_SIZE = 50;

    private final PostRepository posts;
    private final CategoryService categories;
    private final ImageKitService imageKit;

    PostService(PostRepository posts, CategoryService categories, ImageKitService imageKit) {
        this.posts = posts;
        this.categories = categories;
        this.imageKit = imageKit;
    }

    public PageResponse<PostSummary> listVisible(String category, int page, int size) {
        PageRequest request = PageRequest.of(Math.max(page, 0), clamp(size),
                Sort.by(Sort.Order.desc("publishedAt"), Sort.Order.desc("id")));
        String categoryFilter = category == null || category.isBlank() ? null : category;
        return PageResponse.of(posts.findVisible(PostStatus.PUBLISHED, Instant.now(), categoryFilter, request),
                PostSummary::of);
    }

    public PostResponse getVisible(String slug) {
        return posts.findVisibleBySlug(slug, PostStatus.PUBLISHED, Instant.now()).map(PostResponse::of)
                .orElseThrow(() -> new NotFoundException("Article introuvable."));
    }

    public PageResponse<PostSummary> search(PostStatus status, String q, int page, int size) {
        PageRequest request = PageRequest.of(Math.max(page, 0), clamp(size), Sort.by(Sort.Direction.DESC, "updatedAt"));
        String query = q == null || q.isBlank() ? null : q.trim();
        return PageResponse.of(posts.search(status, query, request), PostSummary::of);
    }

    public PostResponse get(Long id) {
        return PostResponse.of(find(id));
    }

    @Transactional
    public PostResponse create(PostRequest request) {
        Post post = new Post();
        apply(post, request);
        return PostResponse.of(posts.save(post));
    }

    @Transactional
    public PostResponse update(Long id, PostRequest request) {
        Post post = find(id);
        String previousCover = post.getCoverFileId();
        apply(post, request);
        if (previousCover != null && !Objects.equals(previousCover, post.getCoverFileId())) {
            imageKit.deleteAfterCommit(previousCover);
        }
        return PostResponse.of(posts.save(post));
    }

    @Transactional
    public void delete(Long id) {
        Post post = find(id);
        posts.delete(post);
        imageKit.deleteAfterCommit(post.getCoverFileId());
    }

    private Post find(Long id) {
        return posts.findById(id).orElseThrow(() -> new NotFoundException("Article introuvable."));
    }

    private void apply(Post post, PostRequest r) {
        String slug = Slugs.resolve(r.slug(), r.title());
        boolean taken = post.getId() == null ? posts.existsBySlug(slug) : posts.existsBySlugAndIdNot(slug, post.getId());
        if (taken) {
            throw new ConflictException("L'adresse « " + slug + " » est déjà utilisée par un autre article.");
        }
        post.setSlug(slug);
        post.setTitle(r.title().trim());
        post.setExcerpt(r.excerpt().trim());
        post.setContent(r.content());
        post.setCoverUrl(blankToNull(r.cover()));
        post.setCoverFileId(post.getCoverUrl() == null ? null : blankToNull(r.coverFileId()));
        post.setCoverFocus(post.getCoverUrl() == null ? null : r.coverFocus());
        post.setCategory(r.categoryId() == null ? null : categories.find(r.categoryId()));
        post.setStatus(r.status());
        if (r.publishedAt() != null) {
            post.setPublishedAt(r.publishedAt());
        } else if (r.status() == PostStatus.PUBLISHED && post.getPublishedAt() == null) {
            post.setPublishedAt(Instant.now());
        }
    }

    private static int clamp(int size) {
        return Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
