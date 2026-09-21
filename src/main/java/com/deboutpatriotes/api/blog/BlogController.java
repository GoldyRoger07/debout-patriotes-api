package com.deboutpatriotes.api.blog;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deboutpatriotes.api.blog.BlogDtos.CategoryResponse;
import com.deboutpatriotes.api.blog.BlogDtos.PostResponse;
import com.deboutpatriotes.api.blog.BlogDtos.PostSummary;
import com.deboutpatriotes.api.common.PageResponse;

/** Lecture publique du blog : seuls les articles publiés et arrivés à échéance sont exposés. */
@RestController
@RequestMapping("/api")
class BlogController {

    private final PostService posts;
    private final CategoryService categories;

    BlogController(PostService posts, CategoryService categories) {
        this.posts = posts;
        this.categories = categories;
    }

    @GetMapping("/posts")
    PageResponse<PostSummary> list(@RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "12") int size) {
        return posts.listVisible(category, page, size);
    }

    @GetMapping("/posts/{slug}")
    PostResponse get(@PathVariable String slug) {
        return posts.getVisible(slug);
    }

    @GetMapping("/categories")
    List<CategoryResponse> categories() {
        return categories.list();
    }
}
