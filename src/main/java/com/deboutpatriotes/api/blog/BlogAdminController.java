package com.deboutpatriotes.api.blog;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.deboutpatriotes.api.blog.BlogDtos.CategoryRequest;
import com.deboutpatriotes.api.blog.BlogDtos.CategoryResponse;
import com.deboutpatriotes.api.blog.BlogDtos.PostRequest;
import com.deboutpatriotes.api.blog.BlogDtos.PostResponse;
import com.deboutpatriotes.api.blog.BlogDtos.PostSummary;
import com.deboutpatriotes.api.common.PageResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
class BlogAdminController {

    private final PostService posts;
    private final CategoryService categories;

    BlogAdminController(PostService posts, CategoryService categories) {
        this.posts = posts;
        this.categories = categories;
    }

    // --- Articles ------------------------------------------------------------

    @GetMapping("/posts")
    PageResponse<PostSummary> list(@RequestParam(required = false) PostStatus status,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return posts.search(status, q, page, size);
    }

    @GetMapping("/posts/{id}")
    PostResponse get(@PathVariable Long id) {
        return posts.get(id);
    }

    @PostMapping("/posts")
    @ResponseStatus(HttpStatus.CREATED)
    PostResponse create(@Valid @RequestBody PostRequest request) {
        return posts.create(request);
    }

    @PutMapping("/posts/{id}")
    PostResponse update(@PathVariable Long id, @Valid @RequestBody PostRequest request) {
        return posts.update(id, request);
    }

    @DeleteMapping("/posts/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Long id) {
        posts.delete(id);
    }

    // --- Rubriques -----------------------------------------------------------

    @GetMapping("/categories")
    List<CategoryResponse> categories() {
        return categories.listWithCounts();
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    CategoryResponse createCategory(@Valid @RequestBody CategoryRequest request) {
        return categories.create(request);
    }

    @PutMapping("/categories/{id}")
    CategoryResponse updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return categories.update(id, request);
    }

    @DeleteMapping("/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCategory(@PathVariable Long id) {
        categories.delete(id);
    }
}
