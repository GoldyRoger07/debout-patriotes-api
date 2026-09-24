package com.deboutpatriotes.api.media;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Cadrage d'une photo, choisi dans le back-office.
 *
 * <p>Les gabarits du site imposent la proportion de chaque emplacement (carte d'article en 16/9,
 * portrait de candidat en 4/5…) : ce réglage ne la change pas, il décide de ce qui est conservé de
 * la photo quand elle y est recadrée — et, avec {@link #CONTAIN}, de ne rien recadrer du tout.
 *
 * <p>Le site Angular traduit chaque valeur en transformation ImageKit (voir `ImageKitPipe`) ; les
 * noms JSON sont en minuscules pour correspondre au type `ImageFocus` du front.
 */
public enum ImageFocus {

    /** ImageKit détecte le sujet et recadre autour. */
    AUTO,
    /** Recadre autour du visage détecté. */
    FACE,
    CENTER,
    TOP,
    BOTTOM,
    LEFT,
    RIGHT,
    /** Aucun recadrage : la photo entre en entier, complétée par des bandes. */
    CONTAIN;

    @JsonValue
    public String jsonValue() {
        return name().toLowerCase();
    }

    @JsonCreator
    public static ImageFocus fromJson(String value) {
        return value == null || value.isBlank() ? null : valueOf(value.trim().toUpperCase());
    }
}
