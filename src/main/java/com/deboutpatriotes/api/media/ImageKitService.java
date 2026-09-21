package com.deboutpatriotes.api.media;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import com.deboutpatriotes.api.common.BadRequestException;
import com.deboutpatriotes.api.common.ConflictException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Client minimal de l'API ImageKit.io : la médiathèque du site. Téléversement, inventaire d'un
 * dossier — pour réutiliser une image déjà en ligne plutôt que de la téléverser deux fois — et
 * suppression.
 *
 * @see <a href="https://imagekit.io/docs/api-reference/upload-file/upload-file">Upload file</a>
 * @see <a href=
 *      "https://imagekit.io/docs/api-reference/digital-asset-management/list-and-search-assets">List
 *      and search assets</a>
 */
@Service
public class ImageKitService {

    private static final Logger log = LoggerFactory.getLogger(ImageKitService.class);

    private static final String UPLOAD_URL = "https://upload.imagekit.io/api/v1/files/upload";
    private static final String FILES_URL = "https://api.imagekit.io/v1/files/{fileId}";
    private static final String DETAILS_URL = "https://api.imagekit.io/v1/files/{fileId}/details";
    private static final String LIST_URL = "https://api.imagekit.io/v1/files";

    /** Formats matriciels uniquement : le SVG peut embarquer du script. */
    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp", "image/gif", "image/avif");

    /** Sous-dossiers acceptés, pour ranger la médiathèque par usage. */
    private static final Set<String> ALLOWED_FOLDERS = Set.of("candidats", "blog", "divers");

    private static final String DEFAULT_FOLDER = "divers";

    /** Plafond d'une page de médiathèque, quoi qu'en demande l'appelant. */
    private static final int MAX_PAGE_SIZE = 100;

    private final ImageKitProperties properties;
    private final ObjectProvider<ImageReferences> references;
    private final RestClient client = RestClient.create();

    public ImageKitService(ImageKitProperties properties, ObjectProvider<ImageReferences> references) {
        this.properties = properties;
        this.references = references;
    }

    public UploadedImage upload(MultipartFile file, String folder) {
        requireConfigured();
        if (file.isEmpty()) {
            throw new BadRequestException("Le fichier est vide.");
        }
        if (file.getContentType() == null || !ALLOWED_TYPES.contains(file.getContentType())) {
            throw new BadRequestException("Format non pris en charge : JPEG, PNG, WebP, GIF ou AVIF uniquement.");
        }
        String target = normalizeFolder(folder);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());
        body.add("fileName", sanitizeFileName(file.getOriginalFilename()));
        body.add("folder", properties.folder() + "/" + target);
        body.add("useUniqueFileName", "true");

        try {
            ImageKitFile uploaded = client.post()
                    .uri(UPLOAD_URL)
                    .header(HttpHeaders.AUTHORIZATION, authorization())
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(ImageKitFile.class);
            if (uploaded == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Réponse vide d'ImageKit.");
            }
            return new UploadedImage(uploaded.fileId(), uploaded.url(), uploaded.thumbnail(), uploaded.name(),
                    uploaded.width(), uploaded.height());
        } catch (RestClientException e) {
            log.error("Échec du téléversement vers ImageKit", e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Le téléversement vers ImageKit a échoué.");
        }
    }

    /**
     * Images d'un dossier de la médiathèque, de la plus récente à la plus ancienne.
     *
     * @param folder sous-dossier : {@code blog}, {@code candidats} ou {@code divers}
     * @param query  filtre sur le nom du fichier, facultatif
     * @param skip   nombre d'images à passer — pagination « charger plus »
     * @param limit  nombre d'images à renvoyer
     */
    public List<GalleryImage> list(String folder, String query, int skip, int limit) {
        requireConfigured();
        String target = normalizeFolder(folder);
        URI uri = UriComponentsBuilder.fromUriString(LIST_URL)
                .queryParam("type", "file")
                .queryParam("fileType", "image")
                .queryParam("path", properties.folder() + "/" + target)
                .queryParam("sort", "DESC_CREATED")
                .queryParam("skip", Math.max(skip, 0))
                .queryParam("limit", Math.clamp(limit, 1, MAX_PAGE_SIZE))
                .queryParamIfPresent("searchQuery", Optional.ofNullable(searchQuery(query)))
                .build()
                .encode()
                .toUri();

        ImageKitFile[] files;
        try {
            files = client.get()
                    .uri(uri)
                    .header(HttpHeaders.AUTHORIZATION, authorization())
                    .retrieve()
                    .body(ImageKitFile[].class);
        } catch (RestClientException e) {
            log.error("Échec de la lecture de la médiathèque ImageKit", e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "La médiathèque ImageKit est injoignable.");
        }

        List<ImageKitFile> found = files == null ? List.of() : Arrays.asList(files);
        Set<String> used = referenced(found.stream().map(ImageKitFile::fileId).toList());
        return found.stream()
                .map(f -> new GalleryImage(f.fileId(), f.name(), f.url(), f.thumbnail(), f.width(), f.height(),
                        f.size(), f.createdAt(), target, used.contains(f.fileId())))
                .toList();
    }

    /**
     * Retire une image de la médiathèque. Une image encore utilisée par un article ou une fiche
     * candidat est conservée : la médiathèque étant partagée, la supprimer casserait ce contenu.
     */
    public void delete(String fileId) {
        requireConfigured();
        String usedBy = usedBy(fileId);
        if (usedBy != null) {
            throw new ConflictException("Cette image est utilisée par " + usedBy
                    + ". Remplacez-la dans ce contenu avant de la supprimer.");
        }
        deleteOnImageKit(fileId);
    }

    /**
     * Suppression « au mieux » d'une image devenue orpheline (remplacée ou dont le contenu a été supprimé) :
     * un échec est journalisé sans faire échouer l'opération métier.
     */
    public void deleteQuietly(String fileId) {
        if (fileId == null || fileId.isBlank() || !properties.configured()) {
            return;
        }
        String usedBy = usedBy(fileId);
        if (usedBy != null) {
            // Image choisie dans la médiathèque, donc partagée : elle sert encore ailleurs.
            log.debug("Image ImageKit {} conservée : encore utilisée par {}.", fileId, usedBy);
            return;
        }
        try {
            deleteOnImageKit(fileId);
        } catch (ResponseStatusException e) {
            log.warn("Image ImageKit {} non supprimée ; à nettoyer depuis la médiathèque.", fileId);
        }
    }

    /**
     * Programme la suppression d'une image après la validation de la transaction en cours : si
     * l'enregistrement échoue, l'image encore référencée n'est pas perdue.
     */
    public void deleteAfterCommit(String fileId) {
        if (fileId == null || fileId.isBlank()) {
            return;
        }
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    deleteQuietly(fileId);
                }
            });
        } else {
            deleteQuietly(fileId);
        }
    }

    private void deleteOnImageKit(String fileId) {
        try {
            client.delete()
                    .uri(FILES_URL, fileId)
                    .header(HttpHeaders.AUTHORIZATION, authorization())
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Échec de la suppression ImageKit du fichier {}", fileId, e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "La suppression sur ImageKit a échoué.");
        }
    }

    /** Libellé du contenu qui utilise encore cette image, ou {@code null} si elle est orpheline. */
    private String usedBy(String fileId) {
        String url = urlOf(fileId);
        return references.orderedStream()
                .map(module -> module.usedBy(fileId, url))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private Set<String> referenced(Collection<String> fileIds) {
        Set<String> used = new HashSet<>();
        references.orderedStream().forEach(module -> used.addAll(module.referenced(fileIds)));
        return used;
    }

    /**
     * URL publique d'un fichier, pour repérer aussi les images insérées dans le corps d'un article.
     * Au mieux : un échec limite la recherche à l'identifiant plutôt que de bloquer l'opération.
     */
    private String urlOf(String fileId) {
        try {
            ImageKitFile file = client.get()
                    .uri(DETAILS_URL, fileId)
                    .header(HttpHeaders.AUTHORIZATION, authorization())
                    .retrieve()
                    .body(ImageKitFile.class);
            return file == null ? null : file.url();
        } catch (RestClientException e) {
            log.warn("Détails ImageKit indisponibles pour {} : recherche limitée à l'identifiant.", fileId);
            return null;
        }
    }

    private void requireConfigured() {
        if (!properties.configured()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "ImageKit n'est pas configuré (IMAGEKIT_PRIVATE_KEY manquant).");
        }
    }

    private String authorization() {
        String credentials = properties.privateKey() + ":";
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    private static String normalizeFolder(String folder) {
        return folder != null && ALLOWED_FOLDERS.contains(folder) ? folder : DEFAULT_FOLDER;
    }

    /** Recherche « contient » sur le nom du fichier, dans le langage de requête d'ImageKit. */
    private static String searchQuery(String query) {
        if (query == null || query.isBlank()) {
            return null;
        }
        // Guillemets et antislashs casseraient la requête, et n'ont pas leur place dans un nom.
        String term = query.trim().replace("\\", "").replace("\"", "");
        return term.isEmpty() ? null : "name : \"" + term + "\"";
    }

    private static String sanitizeFileName(String original) {
        String name = original == null || original.isBlank() ? "image" : original;
        name = name.replaceAll("[^A-Za-z0-9._-]", "_");
        return name.length() > 100 ? name.substring(name.length() - 100) : name;
    }

    public record UploadedImage(String fileId, String url, String thumbnailUrl, String name, Integer width,
            Integer height) {
    }

    /** Une image de la médiathèque, avec l'indication qu'un contenu s'en sert déjà. */
    public record GalleryImage(String fileId, String name, String url, String thumbnailUrl, Integer width,
            Integer height, Long size, String createdAt, String folder, boolean inUse) {
    }

    /**
     * Le téléversement renvoie la vignette dans {@code thumbnailUrl}, l'inventaire dans
     * {@code thumbnail} : les deux sont lus et {@link #thumbnail()} donne celui qui est présent.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    record ImageKitFile(String fileId, String name, String url, String thumbnailUrl, String thumbnail, Integer width,
            Integer height, Long size, String createdAt) {

        @Override
        public String thumbnail() {
            return thumbnail != null ? thumbnail : thumbnailUrl;
        }
    }
}
