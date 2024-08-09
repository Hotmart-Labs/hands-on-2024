package com.hotmart.handson.controller;

import com.hotmart.handson.model.*;
import com.hotmart.handson.repository.LikeRepository;
import com.hotmart.handson.repository.TweetRepository;
import com.hotmart.handson.repository.UserRepository;
import com.hotmart.handson.vo.LikeCreateVO;
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
class LikeControllerTest {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private LikeRepository likeRepository;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        tweetRepository.deleteAll();
        likeRepository.deleteAll();
    }

    @Test
    void likeWithValidUserAndTweetShouldReturnCreated() {
        var user = userRepository.save(User.builder()
                .uuid(UUID.randomUUID().toString())
                .name("User Full Name")
                .username("user")
                .stats(UserStats.builder().followers(0L).build())
                .build()
        );
        assertThat(user).isNotNull();

        var tweet = tweetRepository.save(Tweet.builder()
                .uuid(UUID.randomUUID().toString())
                .author(UserRef.builder()
                        .uuid(user.getUuid())
                        .name(user.getName())
                        .username(user.getUsername())
                        .picture(user.getPicture())
                        .verified(user.isVerified())
                        .build())
                .content("content")
                .createdAt(new Date())
                .build()
        );
        assertThat(tweet).isNotNull();

        var request = LikeCreateVO.builder()
                .user(user.getUsername())
                .build();

        var like = webClient.post()
                .uri("/tweets/{tweet}/likes", tweet.getUuid())
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isCreated()
                .returnResult(Like.class).getResponseBody()
                .log()
                .next()
                .block();

        assertThat(like).isNotNull();
        assertThat(like.getUuid()).isNotBlank();
        assertThat(like.getTweet()).isEqualTo(tweet.getUuid());
        assertThat(like.getUser()).isEqualTo(user.getUuid());

        // validate likes count
        var actualLike = tweetRepository.findById(tweet.getUuid()).get();
        assertThat(actualLike).isNotNull();
        assertThat(actualLike.getStats()).isNotNull();
        assertThat(actualLike.getStats().getLikes()).isEqualTo(1L);
    }

    @Test
    void likeWithInvalidUserOrTweetShouldReturnNotFound() {
        var request = LikeCreateVO.builder()
                .user("user")
                .build();

        webClient.post()
                .uri("/tweets/{tweet}/likes", "invalid_tweet")
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void dislikeWithValidUserAndTweetShouldReturnOk() {
        var user = userRepository.save(User.builder()
                .uuid(UUID.randomUUID().toString())
                .name("User Full Name")
                .username("user")
                .stats(UserStats.builder().followers(0L).build())
                .build()
        );
        assertThat(user).isNotNull();

        var tweet = tweetRepository.save(Tweet.builder()
                .uuid(UUID.randomUUID().toString())
                .author(UserRef.builder()
                        .uuid(user.getUuid())
                        .name(user.getName())
                        .username(user.getUsername())
                        .picture(user.getPicture())
                        .verified(user.isVerified())
                        .build())
                .content("content")
                .createdAt(new Date())
                .stats(TweetStats.builder().likes(1L).build())
                .build()
        );
        assertThat(tweet).isNotNull();

        var like = likeRepository.save(Like.builder()
                .uuid(UUID.randomUUID().toString())
                .tweet(tweet.getUuid())
                .user(user.getUuid())
                .createdAt(new Date())
                .build()
        );
        assertThat(like).isNotNull();

        var dislikeFlux = webClient.delete()
                .uri("/tweets/{tweet}/likes/{username}", tweet.getUuid(), user.getUsername())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Like.class).getResponseBody()
                .log()
                .next().block();

        assertThat(dislikeFlux).isNotNull();
        assertThat(dislikeFlux.getUuid()).isEqualTo(like.getUuid());
        assertThat(dislikeFlux.getTweet()).isEqualTo(tweet.getUuid());
        assertThat(dislikeFlux.getUser()).isEqualTo(user.getUuid());

        // validate likes count
        var actualLike = tweetRepository.findById(tweet.getUuid()).get();
        assertThat(actualLike).isNotNull();
        assertThat(actualLike.getStats()).isNotNull();
        assertThat(actualLike.getStats().getLikes()).isEqualTo(0L);
    }

    @Test
    void dislikeWithInvalidUserOrTweetShouldReturnNotFound() {
        webClient.delete()
                .uri("/tweets/{tweet}/likes/{username}", "invalid_tweet", "invalid_user")
                .exchange()
                .expectStatus().isNotFound();
    }
}
