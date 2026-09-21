-- Schéma initial : comptes d'administration, blog (catégories + articles) et candidats.

CREATE TABLE admin_user (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    email         VARCHAR(190) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    display_name  VARCHAR(120) NOT NULL,
    created_at    DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_admin_user_email UNIQUE (email)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE category (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    name          VARCHAR(80)  NOT NULL,
    slug          VARCHAR(100) NOT NULL,
    display_order INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uk_category_name UNIQUE (name),
    CONSTRAINT uk_category_slug UNIQUE (slug)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE post (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    slug          VARCHAR(190) NOT NULL,
    title         VARCHAR(255) NOT NULL,
    excerpt       VARCHAR(600) NOT NULL,
    content       LONGTEXT     NOT NULL,
    cover_url     VARCHAR(500),
    cover_file_id VARCHAR(100),
    category_id   BIGINT,
    status        VARCHAR(20)  NOT NULL,
    published_at  DATETIME(6),
    created_at    DATETIME(6)  NOT NULL,
    updated_at    DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_post_slug UNIQUE (slug),
    CONSTRAINT fk_post_category FOREIGN KEY (category_id) REFERENCES category (id) ON DELETE SET NULL,
    INDEX idx_post_status_published (status, published_at)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE candidate (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    slug          VARCHAR(190) NOT NULL,
    name          VARCHAR(160) NOT NULL,
    subtitle      VARCHAR(255) NOT NULL,
    photo_url     VARCHAR(500),
    photo_file_id VARCHAR(100),
    position      VARCHAR(160) NOT NULL,
    constituency  VARCHAR(160) NOT NULL,
    party         VARCHAR(160) NOT NULL,
    profession    VARCHAR(160) NOT NULL,
    birthplace    VARCHAR(160) NOT NULL,
    quote         VARCHAR(500),
    email         VARCHAR(190),
    facebook      VARCHAR(255),
    x             VARCHAR(255),
    instagram     VARCHAR(255),
    display_order INT          NOT NULL DEFAULT 0,
    published     BIT(1)       NOT NULL DEFAULT b'1',
    created_at    DATETIME(6)  NOT NULL,
    updated_at    DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_candidate_slug UNIQUE (slug),
    INDEX idx_candidate_order (published, display_order)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE candidate_bio (
    candidate_id BIGINT NOT NULL,
    sort_order    INT    NOT NULL,
    paragraph    TEXT   NOT NULL,
    PRIMARY KEY (candidate_id, sort_order),
    CONSTRAINT fk_candidate_bio FOREIGN KEY (candidate_id) REFERENCES candidate (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE candidate_priority (
    candidate_id BIGINT       NOT NULL,
    sort_order    INT          NOT NULL,
    title        VARCHAR(160) NOT NULL,
    description  TEXT         NOT NULL,
    icon         VARCHAR(60),
    PRIMARY KEY (candidate_id, sort_order),
    CONSTRAINT fk_candidate_priority FOREIGN KEY (candidate_id) REFERENCES candidate (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE candidate_career (
    candidate_id BIGINT       NOT NULL,
    sort_order    INT          NOT NULL,
    period       VARCHAR(60)  NOT NULL,
    title        VARCHAR(255) NOT NULL,
    description  TEXT,
    PRIMARY KEY (candidate_id, sort_order),
    CONSTRAINT fk_candidate_career FOREIGN KEY (candidate_id) REFERENCES candidate (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE candidate_education (
    candidate_id BIGINT       NOT NULL,
    sort_order    INT          NOT NULL,
    entry        VARCHAR(500) NOT NULL,
    PRIMARY KEY (candidate_id, sort_order),
    CONSTRAINT fk_candidate_education FOREIGN KEY (candidate_id) REFERENCES candidate (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Catégories reprises du site (le filtre « Tout » est géré côté front).
INSERT INTO category (name, slug, display_order) VALUES
    ('Communiqués', 'communiques', 1),
    ('Terrain', 'terrain', 2),
    ('Vie du groupement', 'vie-du-groupement', 3),
    ('Élections', 'elections', 4);
