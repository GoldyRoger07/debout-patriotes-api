-- Cadrage des photos, réglé dans le back-office.
-- La proportion de chaque emplacement reste celle du gabarit du site ; ce réglage décide de ce qui
-- est conservé de la photo quand elle y est recadrée. Vide = le cadrage par défaut de l'emplacement.

ALTER TABLE post
    ADD COLUMN cover_focus VARCHAR(20) NULL AFTER cover_file_id;

ALTER TABLE candidate
    ADD COLUMN photo_focus VARCHAR(20) NULL AFTER photo_file_id,
    ADD COLUMN cover_focus VARCHAR(20) NULL AFTER cover_file_id;
