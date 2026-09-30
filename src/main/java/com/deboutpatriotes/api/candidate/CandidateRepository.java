package com.deboutpatriotes.api.candidate;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    List<Candidate> findAllByOrderByDisplayOrderAscIdAsc();

    List<Candidate> findAllByPublishedTrueOrderByDisplayOrderAscIdAsc();

    Optional<Candidate> findBySlugAndPublishedTrue(String slug);

    /** Noms des candidats dont la fiche utilise ce fichier (portrait, couverture ou vidéo). */
    @Query("""
            select c.name from Candidate c
            where :fileId is not null
              and (c.photoFileId = :fileId or c.coverFileId = :fileId or c.videoFileId = :fileId)
            """)
    List<String> findNamesUsingImage(@Param("fileId") String fileId);

    @Query("select c.photoFileId from Candidate c where c.photoFileId in :fileIds")
    List<String> findPhotoFileIdsIn(@Param("fileIds") Collection<String> fileIds);

    @Query("select c.coverFileId from Candidate c where c.coverFileId in :fileIds")
    List<String> findCoverFileIdsIn(@Param("fileIds") Collection<String> fileIds);

    @Query("select c.videoFileId from Candidate c where c.videoFileId in :fileIds")
    List<String> findVideoFileIdsIn(@Param("fileIds") Collection<String> fileIds);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    long countByPublishedTrue();

    @Query("select coalesce(max(c.displayOrder), 0) from Candidate c")
    int maxDisplayOrder();
}
