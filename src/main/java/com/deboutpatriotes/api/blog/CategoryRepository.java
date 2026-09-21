package com.deboutpatriotes.api.blog;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByOrderByDisplayOrderAscNameAsc();

    boolean existsBySlugAndIdNot(String slug, Long id);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsBySlug(String slug);

    boolean existsByNameIgnoreCase(String name);

    @Query("select coalesce(max(c.displayOrder), 0) from Category c")
    int maxDisplayOrder();
}
