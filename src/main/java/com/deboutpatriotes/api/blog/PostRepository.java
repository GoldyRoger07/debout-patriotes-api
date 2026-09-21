package com.deboutpatriotes.api.blog;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    /** Articles visibles sur le site : publiés, dont la date de publication est atteinte. */
    @Query(value = """
            select p from Post p left join fetch p.category c
            where p.status = :status and p.publishedAt <= :now
              and (:category is null or c.slug = :category)
            """, countQuery = """
            select count(p) from Post p left join p.category c
            where p.status = :status and p.publishedAt <= :now
              and (:category is null or c.slug = :category)
            """)
    Page<Post> findVisible(@Param("status") PostStatus status, @Param("now") Instant now,
            @Param("category") String category, Pageable pageable);

    @Query("""
            select p from Post p left join fetch p.category
            where p.slug = :slug and p.status = :status and p.publishedAt <= :now
            """)
    Optional<Post> findVisibleBySlug(@Param("slug") String slug, @Param("status") PostStatus status,
            @Param("now") Instant now);

    @Query(value = """
            select p from Post p left join fetch p.category
            where (:status is null or p.status = :status)
              and (:q is null or lower(p.title) like lower(concat('%', :q, '%')))
            """, countQuery = """
            select count(p) from Post p
            where (:status is null or p.status = :status)
              and (:q is null or lower(p.title) like lower(concat('%', :q, '%')))
            """)
    Page<Post> search(@Param("status") PostStatus status, @Param("q") String q, Pageable pageable);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    long countByStatus(PostStatus status);

    @Query("select p.category.id, count(p) from Post p where p.category is not null group by p.category.id")
    List<Object[]> countByCategory();
}
