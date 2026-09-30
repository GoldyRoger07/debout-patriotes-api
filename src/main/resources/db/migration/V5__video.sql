-- Vidéo hébergée sur ImageKit : une par article (lue en tête de l'article, la couverture en affiche)
-- et une par fiche candidat (section « En vidéo »). L'identifiant permet de la supprimer quand elle
-- est remplacée ou que le contenu disparaît.

ALTER TABLE post
    ADD COLUMN video_url     VARCHAR(500) NULL AFTER cover_focus,
    ADD COLUMN video_file_id VARCHAR(100) NULL AFTER video_url;

ALTER TABLE candidate
    ADD COLUMN video_url     VARCHAR(500) NULL AFTER cover_focus,
    ADD COLUMN video_file_id VARCHAR(100) NULL AFTER video_url;
