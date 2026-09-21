package com.deboutpatriotes.api.common;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;

/** Page de résultats au format JSON stable (indépendant de la sérialisation de `PageImpl`). */
public record PageResponse<T>(List<T> items, int page, int size, long totalItems, int totalPages) {

    public static <E, T> PageResponse<T> of(Page<E> page, Function<E, T> mapper) {
        return new PageResponse<>(page.map(mapper).getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }
}
