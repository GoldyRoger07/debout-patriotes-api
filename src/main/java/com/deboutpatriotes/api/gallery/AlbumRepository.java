package com.deboutpatriotes.api.gallery;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlbumRepository extends JpaRepository<Album, Long> {

    /** Albums du plus récent au plus ancien ; ceux sans date passent après. */
    @Query("select a from Album a where a.published = true order by a.takenOn desc nulls last, a.id desc")
    List<Album> findPublished();

    @Query("select a from Album a order by a.takenOn desc nulls last, a.id desc")
    List<Album> findAllOrdered();

    Optional<Album> findBySlugAndPublishedTrue(String slug);

    /** Titres des albums qui contiennent ce fichier. */
    @Query("select a.title from Album a join a.items i where :fileId is not null and i.fileId = :fileId")
    List<String> findTitlesUsingFile(@Param("fileId") String fileId);

    @Query("select i.fileId from Album a join a.items i where i.fileId in :fileIds")
    List<String> findFileIdsIn(@Param("fileIds") Collection<String> fileIds);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    long countByPublishedTrue();
}
