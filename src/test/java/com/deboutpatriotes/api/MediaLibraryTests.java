package com.deboutpatriotes.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import com.deboutpatriotes.api.blog.Post;
import com.deboutpatriotes.api.blog.PostRepository;
import com.deboutpatriotes.api.blog.PostStatus;
import com.deboutpatriotes.api.candidate.Candidate;
import com.deboutpatriotes.api.candidate.CandidateRepository;
import com.deboutpatriotes.api.media.ImageReferences;
import com.jayway.jsonpath.JsonPath;

/**
 * Médiathèque partagée : une image peut illustrer plusieurs contenus, elle n'est donc supprimable
 * que lorsque plus personne ne s'en sert. Ces tests vérifient le repérage de ces références ;
 * les échanges avec ImageKit lui-même ne sont pas couverts (aucune clé en test).
 */
@SpringBootTest
@AutoConfigureMockMvc
class MediaLibraryTests {

    @Autowired
    MockMvcTester mvc;

    @Autowired
    List<ImageReferences> references;

    @Autowired
    PostRepository posts;

    @Autowired
    CandidateRepository candidates;

    String bearer;

    @BeforeEach
    void login() throws Exception {
        MvcTestResult result = mvc.post().uri("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"admin@test.local\",\"password\":\"password123\"}").exchange();
        bearer = "Bearer " + JsonPath.read(result.getResponse().getContentAsString(), "$.token");
    }

    @Test
    void theLibraryIsReservedToSignedInAdministrators() {
        assertThat(mvc.get().uri("/api/admin/images?folder=blog")).hasStatus(HttpStatus.UNAUTHORIZED);
    }

    /** Sans clé ImageKit, l'inventaire répond 503 plutôt que d'échouer silencieusement. */
    @Test
    void theLibraryAnnouncesWhenImageKitIsNotConfigured() {
        assertThat(mvc.get().uri("/api/admin/images?folder=blog").header(HttpHeaders.AUTHORIZATION, bearer))
                .hasStatus(HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Test
    void aCoverImageIsReportedAsUsedByItsPost() {
        posts.save(post("article-couverture", "Rentrée politique", "Texte.", "file-cover"));

        assertThat(usedBy("file-cover", null)).contains("Rentrée politique");
        assertThat(referenced(Set.of("file-cover", "file-libre"))).containsExactly("file-cover");
    }

    /** Une image insérée dans le corps Markdown compte aussi : la supprimer casserait l'article. */
    @Test
    void anImageEmbeddedInTheBodyIsReportedAsUsed() {
        String url = "https://ik.imagekit.io/dp/blog/meeting_abc.jpg";
        posts.save(post("article-illustre", "Meeting du Nord", "Avant\n\n![Salle comble](" + url + ")\n", null));

        assertThat(usedBy("file-embedded", url)).contains("Meeting du Nord");
        // Sans l'URL, une image seulement insérée dans le texte passe inaperçue.
        assertThat(usedBy("file-embedded", null)).isNull();
    }

    @Test
    void aPortraitIsReportedAsUsedByItsCandidate() {
        Candidate candidate = new Candidate();
        candidate.setSlug("jean-baptiste-pierre");
        candidate.setName("Jean-Baptiste Pierre");
        candidate.setSubtitle("Candidat à la députation");
        candidate.setPosition("Député");
        candidate.setConstituency("Nord");
        candidate.setParty("DEBOUT PATRIOTES");
        candidate.setProfession("Agronome");
        candidate.setBirthplace("Cap-Haïtien");
        candidate.setPhotoUrl("https://ik.imagekit.io/dp/candidats/jbp.jpg");
        candidate.setPhotoFileId("file-portrait");
        candidates.save(candidate);

        assertThat(usedBy("file-portrait", null)).contains("Jean-Baptiste Pierre");
    }

    @Test
    void anImageNobodyUsesIsFreeToDelete() {
        assertThat(usedBy("file-orphelin", "https://ik.imagekit.io/dp/divers/orphelin.jpg")).isNull();
        assertThat(referenced(Set.of("file-orphelin"))).isEmpty();
    }

    private String usedBy(String fileId, String url) {
        return references.stream().map(module -> module.usedBy(fileId, url)).filter(java.util.Objects::nonNull)
                .findFirst().orElse(null);
    }

    private Set<String> referenced(Set<String> fileIds) {
        return references.stream().flatMap(module -> module.referenced(fileIds).stream())
                .collect(java.util.stream.Collectors.toSet());
    }

    private static Post post(String slug, String title, String content, String coverFileId) {
        Post post = new Post();
        post.setSlug(slug);
        post.setTitle(title);
        post.setExcerpt("Chapeau.");
        post.setContent(content);
        post.setStatus(PostStatus.DRAFT);
        post.setCoverUrl(coverFileId == null ? null : "https://ik.imagekit.io/dp/blog/" + slug + ".jpg");
        post.setCoverFileId(coverFileId);
        return post;
    }
}
