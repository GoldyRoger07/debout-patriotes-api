-- Proportion des photos des cartes candidats, réglée dans le back-office pour chaque emplacement :
-- toutes les cartes d'un même emplacement partagent les mêmes dimensions.

CREATE TABLE candidate_card_format (
    placement    VARCHAR(20) NOT NULL,
    ratio_width  INT         NOT NULL,
    ratio_height INT         NOT NULL,
    PRIMARY KEY (placement)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

INSERT INTO candidate_card_format (placement, ratio_width, ratio_height) VALUES
    ('home', 4, 5),
    ('list', 4, 5);
