package com.deboutpatriotes.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

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

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTests {

    @Autowired
    MockMvcTester mvc;

    String bearer;

    @BeforeEach
    void login() throws Exception {
        MvcTestResult result = mvc.post().uri("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"admin@test.local\",\"password\":\"password123\"}").exchange();
        assertThat(result).hasStatusOk();
        bearer = "Bearer " + JsonPath.read(result.getResponse().getContentAsString(), "$.token");
    }

    @Test
    void adminEndpointsRequireAToken() {
        assertThat(mvc.get().uri("/api/admin/candidates")).hasStatus(HttpStatus.UNAUTHORIZED);
        assertThat(mvc.get().uri("/api/admin/candidates").header(HttpHeaders.AUTHORIZATION, "Bearer nope"))
                .hasStatus(HttpStatus.UNAUTHORIZED);
        assertThat(mvc.get().uri("/api/admin/candidates").header(HttpHeaders.AUTHORIZATION, bearer)).hasStatusOk();
    }

    /** Condition pour que le rendu serveur Angular transfère les réponses au navigateur. */
    @Test
    void publicReadsAreCacheableButAdminReadsAreNot() {
        assertThat(mvc.get().uri("/api/candidates")).hasStatusOk()
                .headers().hasValue(HttpHeaders.CACHE_CONTROL, "public, max-age=0, must-revalidate");
        assertThat(mvc.get().uri("/api/admin/candidates").header(HttpHeaders.AUTHORIZATION, bearer)).hasStatusOk()
                .headers().satisfies(h -> assertThat(h.getCacheControl()).contains("no-store"));
    }

    @Test
    void cardFormatsAreSetInTheBackOfficeAndReadPublicly() {
        assertThat(mvc.get().uri("/api/candidates/card-formats")).hasStatusOk().bodyJson()
                .extractingPath("$.home.width").isEqualTo(4);

        assertThat(mvc.put().uri("/api/admin/candidates/card-formats").header(HttpHeaders.AUTHORIZATION, bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"home\":{\"width\":1,\"height\":1},\"list\":{\"width\":3,\"height\":4}}"))
                .hasStatusOk();
        assertThat(mvc.get().uri("/api/candidates/card-formats")).hasStatusOk().bodyJson()
                .extractingPath("$.list.height").isEqualTo(4);

        assertThat(mvc.put().uri("/api/admin/candidates/card-formats").header(HttpHeaders.AUTHORIZATION, bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"home\":{\"width\":0,\"height\":1},\"list\":{\"width\":3,\"height\":4}}"))
                .hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(mvc.put().uri("/api/admin/candidates/card-formats").contentType(MediaType.APPLICATION_JSON)
                .content("{}")).hasStatus(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void wrongPasswordIsRejected() {
        assertThat(mvc.post().uri("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"admin@test.local\",\"password\":\"wrong-password\"}"))
                .hasStatus(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void candidateIsOnlyPublicOncePublished() throws Exception {
        String body = """
                {"name":"Marie-Ève Joseph","subtitle":"Candidate au Sénat","position":"Sénatrice",
                 "constituency":"Ouest","party":"DEBOUT PATRIOTES","professions":["Avocate","Enseignante"],"birthplace":"Jacmel",
                 "bio":["Premier paragraphe."],"priorities":[{"title":"Justice","desc":"Réformer.","icon":"pi-shield"}],
                 "career":[{"period":"2020","title":"Avocate"}],"education":["Droit"],
                 "contact":{"email":"mej@example.org"},"published":false}
                """;
        MvcTestResult created = mvc.post().uri("/api/admin/candidates").header(HttpHeaders.AUTHORIZATION, bearer)
                .contentType(MediaType.APPLICATION_JSON).content(body).exchange();
        assertThat(created).hasStatus(HttpStatus.CREATED).bodyJson().extractingPath("$.slug").isEqualTo("marie-eve-joseph");
        Integer id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        assertThat(mvc.get().uri("/api/candidates/marie-eve-joseph")).hasStatus(HttpStatus.NOT_FOUND);

        mvc.put().uri("/api/admin/candidates/" + id).header(HttpHeaders.AUTHORIZATION, bearer)
                .contentType(MediaType.APPLICATION_JSON).content(body.replace("\"published\":false", "\"published\":true"))
                .exchange();

        assertThat(mvc.get().uri("/api/candidates/marie-eve-joseph")).hasStatusOk().bodyJson()
                .extractingPath("$.priorities[0].desc").isEqualTo("Réformer.");
        assertThat(mvc.get().uri("/api/candidates/marie-eve-joseph")).hasStatusOk().bodyJson()
                .extractingPath("$.professions").isEqualTo(java.util.List.of("Avocate", "Enseignante"));

        // Même slug pour un second candidat : conflit.
        assertThat(mvc.post().uri("/api/admin/candidates").header(HttpHeaders.AUTHORIZATION, bearer)
                .contentType(MediaType.APPLICATION_JSON).content(body)).hasStatus(HttpStatus.CONFLICT);
    }

    @Test
    void invalidCandidateReportsFieldErrors() {
        assertThat(mvc.post().uri("/api/admin/candidates").header(HttpHeaders.AUTHORIZATION, bearer)
                .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"\",\"bio\":[]}"))
                .hasStatus(HttpStatus.BAD_REQUEST).bodyJson().extractingPath("$.errors.name").isNotNull();
    }

    @Test
    void postVisibilityFollowsStatusAndPublicationDate() throws Exception {
        String draft = """
                {"title":"Communiqué de test","excerpt":"Résumé","content":"## Titre","status":"DRAFT"}
                """;
        MvcTestResult created = mvc.post().uri("/api/admin/posts").header(HttpHeaders.AUTHORIZATION, bearer)
                .contentType(MediaType.APPLICATION_JSON).content(draft).exchange();
        assertThat(created).hasStatus(HttpStatus.CREATED);
        Integer id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        assertThat(mvc.get().uri("/api/posts/communique-de-test")).hasStatus(HttpStatus.NOT_FOUND);

        String future = Instant.now().plus(2, ChronoUnit.DAYS).toString();
        mvc.put().uri("/api/admin/posts/" + id).header(HttpHeaders.AUTHORIZATION, bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content(draft.replace("\"DRAFT\"", "\"PUBLISHED\",\"publishedAt\":\"" + future + "\"")).exchange();
        assertThat(mvc.get().uri("/api/posts/communique-de-test")).hasStatus(HttpStatus.NOT_FOUND);

        String past = Instant.now().minus(1, ChronoUnit.HOURS).toString();
        mvc.put().uri("/api/admin/posts/" + id).header(HttpHeaders.AUTHORIZATION, bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content(draft.replace("\"DRAFT\"", "\"PUBLISHED\",\"publishedAt\":\"" + past + "\"")).exchange();
        assertThat(mvc.get().uri("/api/posts/communique-de-test")).hasStatusOk();
        assertThat(mvc.get().uri("/api/posts")).hasStatusOk().bodyJson()
                .extractingPath("$.items[0].slug").isEqualTo("communique-de-test");
    }

    @Test
    void imageUploadReportsMissingImageKitConfiguration() {
        assertThat(mvc.post().uri("/api/admin/images").header(HttpHeaders.AUTHORIZATION, bearer)
                .multipart().file("file", new byte[] { 1, 2, 3 }))
                .hasStatus(HttpStatus.SERVICE_UNAVAILABLE);
    }
}
