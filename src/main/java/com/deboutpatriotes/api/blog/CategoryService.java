package com.deboutpatriotes.api.blog;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deboutpatriotes.api.blog.BlogDtos.CategoryRequest;
import com.deboutpatriotes.api.blog.BlogDtos.CategoryResponse;
import com.deboutpatriotes.api.common.ConflictException;
import com.deboutpatriotes.api.common.NotFoundException;
import com.deboutpatriotes.api.common.Slugs;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categories;
    private final PostRepository posts;

    CategoryService(CategoryRepository categories, PostRepository posts) {
        this.categories = categories;
        this.posts = posts;
    }

    public List<CategoryResponse> list() {
        return categories.findAllByOrderByDisplayOrderAscNameAsc().stream().map(CategoryResponse::of).toList();
    }

    /** Liste du back-office, avec le nombre d'articles rattachés à chaque rubrique. */
    public List<CategoryResponse> listWithCounts() {
        Map<Long, Long> counts = posts.countByCategory().stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
        return categories.findAllByOrderByDisplayOrderAscNameAsc().stream()
                .map(c -> CategoryResponse.of(c, counts.getOrDefault(c.getId(), 0L)))
                .toList();
    }

    Category find(Long id) {
        return categories.findById(id).orElseThrow(() -> new NotFoundException("Rubrique introuvable."));
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        Category category = new Category();
        category.setDisplayOrder(categories.maxDisplayOrder() + 1);
        apply(category, request);
        return CategoryResponse.of(categories.save(category), 0L);
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = find(id);
        apply(category, request);
        return CategoryResponse.of(categories.save(category));
    }

    /** Les articles de la rubrique supprimée restent en ligne, sans rubrique (`ON DELETE SET NULL`). */
    @Transactional
    public void delete(Long id) {
        categories.delete(find(id));
    }

    private void apply(Category category, CategoryRequest request) {
        String name = request.name().trim();
        String slug = Slugs.resolve(request.slug(), name);
        Long id = category.getId();
        if (id == null ? categories.existsByNameIgnoreCase(name) : categories.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new ConflictException("Une rubrique porte déjà ce nom.");
        }
        if (id == null ? categories.existsBySlug(slug) : categories.existsBySlugAndIdNot(slug, id)) {
            throw new ConflictException("L'adresse « " + slug + " » est déjà utilisée par une autre rubrique.");
        }
        category.setName(name);
        category.setSlug(slug);
        if (request.displayOrder() != null) {
            category.setDisplayOrder(request.displayOrder());
        }
    }
}
