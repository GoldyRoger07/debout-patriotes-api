-- Fiches candidat : seules les informations réellement saisies sont affichées sur le site.
-- Les champs descriptifs deviennent facultatifs, la profession devient une liste,
-- et une photo de couverture s'ajoute au portrait (cartes de l'accueil et de la liste).

ALTER TABLE candidate
    MODIFY COLUMN subtitle     VARCHAR(255) NULL,
    MODIFY COLUMN `position`   VARCHAR(160) NULL,
    MODIFY COLUMN constituency VARCHAR(160) NULL,
    MODIFY COLUMN party        VARCHAR(160) NULL,
    MODIFY COLUMN birthplace   VARCHAR(160) NULL,
    ADD COLUMN cover_url     VARCHAR(500) NULL AFTER photo_file_id,
    ADD COLUMN cover_file_id VARCHAR(100) NULL AFTER cover_url;

CREATE TABLE candidate_profession (
    candidate_id BIGINT       NOT NULL,
    sort_order   INT          NOT NULL,
    label        VARCHAR(160) NOT NULL,
    PRIMARY KEY (candidate_id, sort_order),
    CONSTRAINT fk_candidate_profession FOREIGN KEY (candidate_id) REFERENCES candidate (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- La profession unique existante devient la première de la liste.
INSERT INTO candidate_profession (candidate_id, sort_order, label)
SELECT id, 0, profession FROM candidate WHERE profession IS NOT NULL AND TRIM(profession) <> '';

ALTER TABLE candidate DROP COLUMN profession;
