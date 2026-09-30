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

/** Galerie (albums de photos et vidéos) et agenda des événements, du back-office au site public. */
@SpringBootTest
@AutoConfigureMockMvc
class GalleryAndEventsTests {

    @Autowired
    MockMvcTester mvc;

    String bearer;

    @BeforeEach
    void login() throws Exception {
        MvcTestResult result = mvc.post().uri("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"admin@test.local\",\"password\":\"password123\"}").exchange();
        bearer = "Bearer " + JsonPath.read(result.getResponse().getContentAsString(), "$.token");
    }

    @Test
    void anAlbumMixesPhotosAndVideosAndIsOnlyPublicOncePublished() throws Exception {
        String body = """
                {"title":"Assemblée de Jacmel","takenOn":"2026-09-12","description":"Retour en images.",
                 "items":[
                   {"type":"image","url":"https://ik.imagekit.io/dp/galerie/salle.jpg","fileId":"f-salle","caption":"La salle"},
                   {"type":"video","url":"https://ik.imagekit.io/dp/galerie/discours.mp4","fileId":"f-discours"},
                   {"type":"image","url":"https://ik.imagekit.io/dp/galerie/foule.jpg","fileId":"f-foule"}
                 ],"published":false}
                """;
        MvcTestResult created = mvc.post().uri("/api/admin/albums").header(HttpHeaders.AUTHORIZATION, bearer)
                .contentType(MediaType.APPLICATION_JSON).content(body).exchange();
        assertThat(created).hasStatus(HttpStatus.CREATED).bodyJson().extractingPath("$.slug")
                .isEqualTo("assemblee-de-jacmel");
        Integer id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        assertThat(mvc.get().uri("/api/albums/assemblee-de-jacmel")).hasStatus(HttpStatus.NOT_FOUND);

        mvc.put().uri("/api/admin/albums/" + id).header(HttpHeaders.AUTHORIZATION, bearer)
                .contentType(MediaType.APPLICATION_JSON).content(body.replace("\"published\":false", "\"published\":true"))
                .exchange();

        assertThat(mvc.get().uri("/api/albums")).hasStatusOk()
                .headers().hasValue(HttpHeaders.CACHE_CONTROL, "public, max-age=0, must-revalidate");
        assertThat(mvc.get().uri("/api/albums")).bodyJson().extractingPath("$[0].photoCount").isEqualTo(2);
        assertThat(mvc.get().uri("/api/albums")).bodyJson().extractingPath("$[0].videoCount").isEqualTo(1);
        assertThat(mvc.get().uri("/api/albums")).bodyJson().extractingPath("$[0].cover.caption").isEqualTo("La salle");
        assertThat(mvc.get().uri("/api/albums/assemblee-de-jacmel")).hasStatusOk().bodyJson()
                .extractingPath("$.items[1].type").isEqualTo("video");
    }

    @Test
    void anAlbumItemNeedsAKnownType() {
        assertThat(mvc.post().uri("/api/admin/albums").header(HttpHeaders.AUTHORIZATION, bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Sans type\",\"items\":[{\"url\":\"https://ik.imagekit.io/x.jpg\"}]}"))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void eventsArePublicOncePublishedWithTheirVideo() throws Exception {
        String startsAt = Instant.now().minus(3, ChronoUnit.DAYS).toString();
        String body = """
                {"title":"Meeting du Cap","kind":"Meeting","startsAt":"%s","place":"Place d'Armes","city":"Cap-Haïtien",
                 "video":"https://ik.imagekit.io/dp/evenements/meeting.mp4","videoFileId":"f-meeting","published":false}
                """.formatted(startsAt);
        MvcTestResult created = mvc.post().uri("/api/admin/events").header(HttpHeaders.AUTHORIZATION, bearer)
                .contentType(MediaType.APPLICATION_JSON).content(body).exchange();
        assertThat(created).hasStatus(HttpStatus.CREATED);
        Integer id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        assertThat(mvc.get().uri("/api/events")).hasStatusOk().bodyJson().extractingPath("$.length()").isEqualTo(0);

        mvc.put().uri("/api/admin/events/" + id).header(HttpHeaders.AUTHORIZATION, bearer)
                .contentType(MediaType.APPLICATION_JSON).content(body.replace("\"published\":false", "\"published\":true"))
                .exchange();

        assertThat(mvc.get().uri("/api/events")).hasStatusOk().bodyJson()
                .extractingPath("$[0].video").isEqualTo("https://ik.imagekit.io/dp/evenements/meeting.mp4");
        assertThat(mvc.get().uri("/api/admin/events")).hasStatus(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void anEventNeedsADate() {
        assertThat(mvc.post().uri("/api/admin/events").header(HttpHeaders.AUTHORIZATION, bearer)
                .contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Sans date\"}"))
                .hasStatus(HttpStatus.BAD_REQUEST).bodyJson().extractingPath("$.errors.startsAt").isNotNull();
    }
}
