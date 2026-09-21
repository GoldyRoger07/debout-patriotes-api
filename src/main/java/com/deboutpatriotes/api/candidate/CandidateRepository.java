package com.deboutpatriotes.api.candidate;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    List<Candidate> findAllByOrderByDisplayOrderAscIdAsc();

    List<Candidate> findAllByPublishedTrueOrderByDisplayOrderAscIdAsc();

    Optional<Candidate> findBySlugAndPublishedTrue(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    long countByPublishedTrue();

    @Query("select coalesce(max(c.displayOrder), 0) from Candidate c")
    int maxDisplayOrder();
}
