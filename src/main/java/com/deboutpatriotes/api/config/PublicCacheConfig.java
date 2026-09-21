package com.deboutpatriotes.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * En-tête de cache des lectures publiques.
 *
 * Spring Security marque par défaut toute réponse `no-store`, ce qui empêche le rendu serveur Angular
 * de transmettre au navigateur les réponses obtenues pendant le rendu : chaque page appelait alors
 * l'API deux fois. `public, max-age=0, must-revalidate` reste toujours frais (une modification du
 * back-office est visible immédiatement) tout en autorisant ce transfert. Security n'écrase pas un
 * `Cache-Control` déjà posé ; l'espace d'administration garde donc son `no-store`.
 */
@Configuration
class PublicCacheConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                if ("GET".equals(request.getMethod())) {
                    response.setHeader(HttpHeaders.CACHE_CONTROL, "public, max-age=0, must-revalidate");
                }
                return true;
            }
        }).addPathPatterns("/api/candidates/**", "/api/posts/**", "/api/categories/**");
    }
}
