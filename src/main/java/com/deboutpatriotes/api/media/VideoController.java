package com.deboutpatriotes.api.media;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** Vidéos des articles et des fiches candidat, hébergées sur ImageKit comme les images. */
@RestController
@RequestMapping("/api/admin/videos")
class VideoController {

    private final ImageKitService imageKit;

    VideoController(ImageKitService imageKit) {
        this.imageKit = imageKit;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ImageKitService.UploadedVideo upload(@RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "divers") String folder) {
        return imageKit.uploadVideo(file, folder);
    }

    /**
     * Abandon d'une vidéo téléversée mais jamais enregistrée (formulaire annulé, vidéo remplacée).
     * Refusé — 409 — si un article ou une fiche candidat l'utilise encore.
     */
    @DeleteMapping("/{fileId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable String fileId) {
        imageKit.delete(fileId);
    }
}
