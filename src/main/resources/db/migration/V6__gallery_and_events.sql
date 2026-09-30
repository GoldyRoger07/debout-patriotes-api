-- Galerie : albums de photos et de vidéos hébergées sur ImageKit, affichés sur `/galerie`.
-- Agenda : événements du groupement, affichés sur `/evenements`, avec une couverture et une vidéo
-- (annonce ou rediffusion) facultatives.

CREATE TABLE album (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    slug        VARCHAR(190) NOT NULL,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    taken_on    DATE,
    published   BIT(1)       NOT NULL DEFAULT b'1',
    created_at  DATETIME(6)  NOT NULL,
    updated_at  DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_album_slug UNIQUE (slug),
    INDEX idx_album_published (published, taken_on)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE album_item (
    album_id   BIGINT       NOT NULL,
    sort_order INT          NOT NULL,
    media_type VARCHAR(10)  NOT NULL,
    url        VARCHAR(500) NOT NULL,
    file_id    VARCHAR(100),
    caption    VARCHAR(255),
    PRIMARY KEY (album_id, sort_order),
    INDEX idx_album_item_file (file_id),
    CONSTRAINT fk_album_item FOREIGN KEY (album_id) REFERENCES album (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE agenda_event (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    slug          VARCHAR(190) NOT NULL,
    title         VARCHAR(255) NOT NULL,
    kind          VARCHAR(80),
    description   TEXT,
    starts_at     DATETIME(6)  NOT NULL,
    place         VARCHAR(255),
    city          VARCHAR(160),
    cover_url     VARCHAR(500),
    cover_file_id VARCHAR(100),
    cover_focus   VARCHAR(20),
    video_url     VARCHAR(500),
    video_file_id VARCHAR(100),
    published     BIT(1)       NOT NULL DEFAULT b'1',
    created_at    DATETIME(6)  NOT NULL,
    updated_at    DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_agenda_event_slug UNIQUE (slug),
    INDEX idx_agenda_event_published (published, starts_at)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
