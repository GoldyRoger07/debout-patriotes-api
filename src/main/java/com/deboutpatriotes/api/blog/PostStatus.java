package com.deboutpatriotes.api.blog;

public enum PostStatus {
    /** Brouillon : visible uniquement dans le back-office. */
    DRAFT,
    /** Publié : visible sur le site dès que `publishedAt` est atteint (permet la programmation). */
    PUBLISHED
}
