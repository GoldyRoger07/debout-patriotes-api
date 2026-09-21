package com.deboutpatriotes.api.media;

import java.util.Collection;
import java.util.Set;

/**
 * Contenu susceptible d'utiliser une image de la médiathèque.
 *
 * <p>Depuis que la médiathèque permet de <em>réutiliser</em> une image déjà téléversée, une même
 * image peut servir à plusieurs contenus : elle n'est supprimée d'ImageKit que si plus personne ne
 * la référence. Chaque module qui stocke des images en fournit une implémentation.
 */
public interface ImageReferences {

    /**
     * Libellé du premier contenu qui utilise cette image, ou {@code null} si aucun.
     *
     * @param fileId identifiant ImageKit du fichier
     * @param url    URL du fichier, pour repérer aussi les images insérées dans un texte ;
     *               {@code null} si elle n'a pas pu être déterminée
     */
    String usedBy(String fileId, String url);

    /** Parmi ces identifiants, ceux que ce module référence — en une seule requête. */
    Set<String> referenced(Collection<String> fileIds);
}
