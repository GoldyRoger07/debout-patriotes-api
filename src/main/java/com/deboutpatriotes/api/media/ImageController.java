package com.deboutpatriotes.api.media;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** Médiathèque du back-office : inventaire, téléversement et suppression des images ImageKit. */
@RestController
@RequestMapping("/api/admin/images")
class ImageController {

    private final ImageKitService imageKit;

    ImageController(ImageKitService imageKit) {
        this.imageKit = imageKit;
    }

    /**
     * Images déjà en ligne dans un dossier, pour en réutiliser une plutôt que de la téléverser
     * à nouveau.
     */
    @GetMapping
    List<ImageKitService.GalleryImage> list(@RequestParam(defaultValue = "divers") String folder,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int skip,
            @RequestParam(defaultValue = "24") int limit) {
        return imageKit.list(folder, q, skip, limit);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ImageKitService.UploadedImage upload(@RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "divers") String folder) {
        return imageKit.upload(file, folder);
    }

    /**
     * Retire une image de la médiathèque : abandon d'un téléversement jamais enregistré
     * (formulaire annulé, image remplacée) ou ménage depuis la médiathèque. Refusé — 409 — si un
     * article ou une fiche candidat l'utilise encore.
     */
    @DeleteMapping("/{fileId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable String fileId) {
        imageKit.delete(fileId);
    }
}
