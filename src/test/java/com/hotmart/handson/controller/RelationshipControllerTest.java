package com.hotmart.handson.controller;

import com.hotmart.handson.model.Relationship;
import com.hotmart.handson.model.User;
import com.hotmart.handson.model.UserStats;
import com.hotmart.handson.repository.RelationshipRepository;
import com.hotmart.handson.repository.UserRepository;
import com.hotmart.handson.vo.RelationshipCreateVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
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
class RelationshipControllerTest {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RelationshipRepository relationshipRepository;


    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        relationshipRepository.deleteAll();
    }

    @Test
    void followWithValidUsersShouldReturnOk() {
        var user = userRepository.save(User.builder()
                .uuid(UUID.randomUUID().toString())
                .name("User Full Name")
                .username("user")
                .stats(UserStats.builder().followers(0L).build())
                .build()
        );
        assertThat(user).isNotNull();

        var follower = userRepository.save(User.builder()
                .uuid(UUID.randomUUID().toString())
                .name("Follower Full Name")
                .username("follower")
                .stats(UserStats.builder().following(0L).build())
                .build()
        );
        assertThat(follower).isNotNull();

        var request = RelationshipCreateVO.builder()
                .follower(follower.getUsername())
                .build();

        var follow = webClient.post()
                .uri("/users/{username}/relationships", user.getUsername())
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isCreated()
                .returnResult(Relationship.class).getResponseBody()
                .log()
                .next()
                .block();

        assertThat(follow).isNotNull();
        assertThat(follow.getUuid()).isNotBlank();
        assertThat(follow.getUser()).isEqualTo(user.getUuid());
        assertThat(follow.getFollower()).isEqualTo(follower.getUuid());

        // validate followers count
        var actualUser = userRepository.findById(user.getUuid()).get();
        assertThat(actualUser).isNotNull();
        assertThat(actualUser.getStats()).isNotNull();
        assertThat(actualUser.getStats().getFollowers()).isEqualTo(1L);

        // validate following count
        var actualFollower = userRepository.findById(follower.getUuid()).get();
        assertThat(actualFollower).isNotNull();
        assertThat(actualFollower.getStats()).isNotNull();
        assertThat(actualFollower.getStats().getFollowing()).isEqualTo(1L);
    }

    @Test
    void followWithInvalidUserShouldReturnNotFound() {
        var request = RelationshipCreateVO.builder()
                .follower("follower")
                .build();

        webClient.post()
                .uri("/users/{username}/relationships", "invalid_user")
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void unfollowWithValidUsersShouldReturnOk() {
        var user = userRepository.save(User.builder()
                .uuid(UUID.randomUUID().toString())
                .name("User Full Name")
                .username("user")
                .stats(UserStats.builder().followers(1L).build())
                .build()
        );
        assertThat(user).isNotNull();

        var follower = userRepository.save(User.builder()
                .uuid(UUID.randomUUID().toString())
                .name("Follower Full Name")
                .username("follower")
                .stats(UserStats.builder().following(1L).build())
                .build()
        );
        assertThat(follower).isNotNull();

        var relationship = relationshipRepository.save(Relationship.builder()
                .uuid(UUID.randomUUID().toString())
                .user(user.getUuid())
                .follower(follower.getUuid())
                .createdAt(new Date())
                .build()
        );
        assertThat(relationship).isNotNull();

        var follow = webClient.delete()
                .uri("/users/{user}/relationships/{follower}", user.getUsername(), follower.getUsername())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Relationship.class).getResponseBody()
                .log()
                .next()
                .block();

        assertThat(follow).isNotNull();
        assertThat(follow.getUuid()).isEqualTo(relationship.getUuid());
        assertThat(follow.getUser()).isEqualTo(user.getUuid());
        assertThat(follow.getFollower()).isEqualTo(follower.getUuid());

        // validate followers count
        var actualUser = userRepository.findById(user.getUuid()).get();
        assertThat(actualUser).isNotNull();
        assertThat(actualUser.getStats()).isNotNull();
        assertThat(actualUser.getStats().getFollowers()).isEqualTo(0L);

        // validate following count
        var actualFollower = userRepository.findById(follower.getUuid()).get();
        assertThat(actualFollower).isNotNull();
        assertThat(actualFollower.getStats()).isNotNull();
        assertThat(actualFollower.getStats().getFollowing()).isEqualTo(0L);
    }

    @Test
    void unfollowWithInvalidUserShouldReturnNotFound() {
        webClient.delete()
                .uri("/users/{user}/relationships/{follower}", "invalid_user", "invalid_follower")
                .exchange()
                .expectStatus().isNotFound();
    }
}
