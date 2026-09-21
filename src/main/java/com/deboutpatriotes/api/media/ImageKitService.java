package com.deboutpatriotes.api.media;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.deboutpatriotes.api.common.BadRequestException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Client minimal de l'API ImageKit.io : téléversement et suppression de fichiers.
 *
 * @see <a href="https://imagekit.io/docs/api-reference/upload-file/upload-file">Upload file</a>
 */
@Service
public class ImageKitService {

    private static final Logger log = LoggerFactory.getLogger(ImageKitService.class);

    private static final String UPLOAD_URL = "https://upload.imagekit.io/api/v1/files/upload";
    private static final String FILES_URL = "https://api.imagekit.io/v1/files/{fileId}";

    /** Formats matriciels uniquement : le SVG peut embarquer du script. */
    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp", "image/gif", "image/avif");

    /** Sous-dossiers acceptés, pour ranger la médiathèque par usage. */
    private static final Set<String> ALLOWED_FOLDERS = Set.of("candidats", "blog", "divers");

    private final ImageKitProperties properties;
    private final RestClient client = RestClient.create();

    public ImageKitService(ImageKitProperties properties) {
        this.properties = properties;
    }

    public UploadedImage upload(MultipartFile file, String folder) {
        requireConfigured();
        if (file.isEmpty()) {
            throw new BadRequestException("Le fichier est vide.");
        }
        if (file.getContentType() == null || !ALLOWED_TYPES.contains(file.getContentType())) {
            throw new BadRequestException("Format non pris en charge : JPEG, PNG, WebP, GIF ou AVIF uniquement.");
        }
        String target = ALLOWED_FOLDERS.contains(folder) ? folder : "divers";

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
            return new UploadedImage(uploaded.fileId(), uploaded.url(), uploaded.thumbnailUrl(), uploaded.name(),
                    uploaded.width(), uploaded.height());
        } catch (RestClientException e) {
            log.error("Échec du téléversement vers ImageKit", e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Le téléversement vers ImageKit a échoué.");
        }
    }

    public void delete(String fileId) {
        requireConfigured();
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

    /**
     * Suppression « au mieux » d'une image devenue orpheline (remplacée ou dont le contenu a été supprimé) :
     * un échec est journalisé sans faire échouer l'opération métier.
     */
    public void deleteQuietly(String fileId) {
        if (fileId == null || fileId.isBlank() || !properties.configured()) {
            return;
        }
        try {
            delete(fileId);
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

    private static String sanitizeFileName(String original) {
        String name = original == null || original.isBlank() ? "image" : original;
        name = name.replaceAll("[^A-Za-z0-9._-]", "_");
        return name.length() > 100 ? name.substring(name.length() - 100) : name;
    }

    public record UploadedImage(String fileId, String url, String thumbnailUrl, String name, Integer width,
            Integer height) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record ImageKitFile(String fileId, String name, String url, String thumbnailUrl, Integer width, Integer height) {
    }
}
