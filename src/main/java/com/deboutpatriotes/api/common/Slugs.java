package com.deboutpatriotes.api.common;

import java.text.Normalizer;
import java.util.Locale;

/** Génération d'identifiants d'URL lisibles : « Élections 2026 » → « elections-2026 ». */
public final class Slugs {

    private Slugs() {
    }

    public static String slugify(String input) {
        if (input == null) {
            return "";
        }
        String ascii = Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return ascii.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-+|-+$)", "");
    }

    /** Slug fourni s'il est renseigné, sinon dérivé du libellé ; refuse un résultat vide. */
    public static String resolve(String requested, String fallbackSource) {
        String slug = slugify(requested == null || requested.isBlank() ? fallbackSource : requested);
        if (slug.isEmpty()) {
            throw new BadRequestException("Impossible de générer un identifiant d'URL valide.");
        }
        return slug;
    }
}
