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

/** Téléversement des images du back-office vers ImageKit. */
@RestController
@RequestMapping("/api/admin/images")
class ImageController {

    private final ImageKitService imageKit;

    ImageController(ImageKitService imageKit) {
        this.imageKit = imageKit;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ImageKitService.UploadedImage upload(@RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "divers") String folder) {
        return imageKit.upload(file, folder);
    }

    /** Abandon d'une image téléversée mais jamais enregistrée (formulaire annulé, image remplacée). */
    @DeleteMapping("/{fileId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable String fileId) {
        imageKit.delete(fileId);
    }
}
