package com.hotmart.handson.controller;

import com.hotmart.handson.model.User;
import com.hotmart.handson.model.UserStats;
import com.hotmart.handson.repository.UserRepository;
import com.hotmart.handson.vo.UserCreateVO;
import com.hotmart.handson.vo.UserVO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient(timeout = "36000")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {

    private static final User user = User.builder()
            .uuid(UUID.randomUUID().toString())
            .name("User Full Name")
            .username("user")
            .joinedAt(new Date())
            .bio("bio")
            .location("Belo Horizonte")
            .link("http://localhost")
            .stats(UserStats.builder().tweets(1L).followers(1L).following(1L).build())
            .build();

    @Autowired
    private WebTestClient webClient;

    @BeforeAll
    public static void init(@Autowired UserRepository userRepository) {
        userRepository.deleteAll();
        userRepository.save(user);
    }

    @Test
    void createUserWithValidRequestShouldReturnCreated() {
        var user = UserCreateVO.builder()
                .username("new_user")
                .name("New User")
                .bio("bio")
                .location("Belo Horizonte")
                .link("http://localhost")
                .build();

        var savedUser = webClient.post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(user))
                .exchange()
                .expectStatus().isCreated()
                .returnResult(UserVO.class).getResponseBody()
                .log()
                .next()
                .block();

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUuid()).isNotBlank();
        assertThat(savedUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(savedUser.getName()).isEqualTo(user.getName());
        assertThat(savedUser.getBio()).isEqualTo(user.getBio());
        assertThat(savedUser.getLocation()).isEqualTo(user.getLocation());
        assertThat(savedUser.getLink()).isEqualTo(user.getLink());

        assertThat(savedUser.getStats()).isNotNull();
        assertThat(savedUser.getStats().getTweets()).isEqualTo(0L);
        assertThat(savedUser.getStats().getFollowers()).isEqualTo(0L);
        assertThat(savedUser.getStats().getFollowing()).isEqualTo(0L);
        assertThat(savedUser.getJoinedAt()).isInThePast();
    }

    @Test
    void createUserWithInvalidRequestShouldReturnBadRequest() {
        var user = UserCreateVO.builder()
                .username("invalid user name")
                .name("Full Name")
                .build();

        webClient.post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(user))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getUserWithValidUserShouldReturnOk(@Autowired UserRepository userRepository) {
        var userFlux = webClient.get()
                .uri("/users/{username}", user.getUsername())
                .exchange()
                .expectStatus().isOk()
                .returnResult(UserVO.class).getResponseBody()
                .log()
                .next()
                .block();

        assertThat(userFlux).isNotNull();
        assertThat(userFlux.getUuid()).isNotBlank();
        assertThat(userFlux.getUsername()).isEqualTo(user.getUsername());
        assertThat(userFlux.getName()).isEqualTo(user.getName());
        assertThat(userFlux.getBio()).isEqualTo(user.getBio());
        assertThat(userFlux.getLocation()).isEqualTo(user.getLocation());
        assertThat(userFlux.getLink()).isEqualTo(user.getLink());
        assertThat(userFlux.getJoinedAt()).isInThePast();

        assertThat(userFlux.getStats()).isNotNull();
        assertThat(userFlux.getStats().getTweets()).isEqualTo(user.getStats().getTweets());
        assertThat(userFlux.getStats().getFollowers()).isEqualTo(user.getStats().getFollowers());
        assertThat(userFlux.getStats().getFollowing()).isEqualTo(user.getStats().getFollowers());
    }

    @Test
    void getUserWithInvalidUserShouldReturnNotFound() {
        webClient.get()
                .uri("/users/{username}", "invalid_user")
                .exchange()
                .expectStatus().isNotFound();
    }
}
