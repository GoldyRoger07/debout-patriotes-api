package com.deboutpatriotes.api.event;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByPublishedTrueOrderByStartsAtDesc();

    List<Event> findAllByOrderByStartsAtDesc();

    /** Titres des événements qui utilisent ce fichier (couverture ou vidéo). */
    @Query("""
            select e.title from Event e
            where :fileId is not null and (e.coverFileId = :fileId or e.videoFileId = :fileId)
            """)
    List<String> findTitlesUsingFile(@Param("fileId") String fileId);

    @Query("select e.coverFileId from Event e where e.coverFileId in :fileIds")
    List<String> findCoverFileIdsIn(@Param("fileIds") Collection<String> fileIds);

    @Query("select e.videoFileId from Event e where e.videoFileId in :fileIds")
    List<String> findVideoFileIdsIn(@Param("fileIds") Collection<String> fileIds);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);
}
