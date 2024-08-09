package com.hotmart.handson.controller;

import com.hotmart.handson.model.*;
import com.hotmart.handson.repository.TweetRepository;
import com.hotmart.handson.repository.UserRepository;
import com.hotmart.handson.vo.ErrorVO;
import com.hotmart.handson.vo.RetweetCreateVO;
import com.hotmart.handson.vo.TweetCreateVO;
import com.hotmart.handson.vo.TweetVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient(timeout = "36000")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TweetControllerTest {

    private static final User user = User.builder()
            .uuid(UUID.randomUUID().toString())
            .name("User Full Name")
            .username("user")
            .build();

    private static final Tweet tweet = Tweet.builder()
            .uuid(UUID.randomUUID().toString())
            .content("content")
            .author(UserRef.builder()
                    .uuid(user.getUuid())
                    .name(user.getName())
                    .username(user.getUsername())
                    .picture(user.getPicture())
                    .verified(user.isVerified())
                    .build())
            .createdAt(new Date())
            .stats(TweetStats.builder().likes(1L).replies(1L).retweets(1L).build())
            .build();

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private TweetRepository tweetRepository;

    @BeforeEach
    public void setUp(@Autowired TweetRepository tweetRepository,
                      @Autowired UserRepository userRepository) {

        // reset database state
        tweetRepository.deleteAll();
        userRepository.deleteAll();

        userRepository.save(user);
        tweetRepository.save(tweet);
    }

    @Test
    void createWithValidRequestShouldReturnCreated() {
        var tweet = TweetCreateVO.builder()
                .username(user.getUsername())
                .content("content")
                .build();

        var body = new MultipartBodyBuilder();
        body.part("tweet", tweet).contentType(MediaType.APPLICATION_JSON);
        body.part("media", new ClassPathResource("tweet.jpg"));

        var tweetSaved = webClient.post()
                .uri("/tweets")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(body.build()))
                .exchange()
                .expectStatus().isCreated()
                .returnResult(TweetVO.class).getResponseBody()
                .log()
                .next()
                .block();

        assertThat(tweetSaved).isNotNull();
        assertThat(tweetSaved.getUuid()).isNotBlank();
        assertThat(tweetSaved.getContent()).isEqualTo(tweet.getContent());
        assertThat(tweetSaved.getCreatedAt()).isInThePast();
        assertThat(tweetSaved.getMedia()).isNotEmpty();
        assertThat(tweetSaved.getParent()).isNull();
        assertThat(tweetSaved.getType()).isEqualTo(TweetType.TWEET.name());

        assertThat(tweetSaved.getAuthor()).isNotNull();
        assertThat(tweetSaved.getAuthor().getUuid()).isEqualTo(user.getUuid());
        assertThat(tweetSaved.getAuthor().getUsername()).isEqualTo(user.getUsername());
        assertThat(tweetSaved.getAuthor().getName()).isEqualTo(user.getName());

        assertThat(tweetSaved.getStats()).isNotNull();
        assertThat(tweetSaved.getStats().getLikes()).isEqualTo(0L);
        assertThat(tweetSaved.getStats().getReplies()).isEqualTo(0L);
        assertThat(tweetSaved.getStats().getRetweets()).isEqualTo(0L);
    }

    @Test
    void createWithValidRequestAndParentTweetShouldReturnCreated() {
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

        var reply = TweetCreateVO.builder()
                .tweetReplied(tweet.getUuid())
                .username(user.getUsername())
                .content("content")
                .build();

        var body = new MultipartBodyBuilder();
        body.part("tweet", reply).contentType(MediaType.APPLICATION_JSON);
        body.part("media", new ClassPathResource("tweet.jpg"));

        var tweetFlux = webClient.post()
                .uri("/tweets")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(body.build()))
                .exchange()
                .expectStatus().isCreated()
                .returnResult(TweetVO.class).getResponseBody()
                .log()
                .next()
                .block();

        assertThat(tweetFlux).isNotNull();
        assertThat(tweetFlux.getUuid()).isNotBlank();
        assertThat(tweetFlux.getContent()).isEqualTo(tweet.getContent());
        assertThat(tweetFlux.getCreatedAt()).isInThePast();
        assertThat(tweetFlux.getMedia()).isNotEmpty();
        assertThat(tweetFlux.getType()).isEqualTo(TweetType.REPLY.name());

        assertThat(tweetFlux.getParent()).isNotNull();
        assertThat(tweetFlux.getParent().getUuid()).isEqualTo(tweet.getUuid());
        assertThat(tweetFlux.getParent().getContent()).isEqualTo(tweet.getContent());
        assertThat(tweetFlux.getParent().getAuthor()).isNotNull();
        assertThat(tweetFlux.getParent().getAuthor().getUuid()).isEqualTo(tweet.getAuthor().getUuid());
        assertThat(tweetFlux.getParent().getAuthor().getUsername()).isEqualTo(tweet.getAuthor().getUsername());
        assertThat(tweetFlux.getParent().getAuthor().getName()).isEqualTo(tweet.getAuthor().getName());

        assertThat(tweetFlux.getAuthor()).isNotNull();
        assertThat(tweetFlux.getAuthor().getUuid()).isEqualTo(user.getUuid());
        assertThat(tweetFlux.getAuthor().getUsername()).isEqualTo(user.getUsername());
        assertThat(tweetFlux.getAuthor().getName()).isEqualTo(user.getName());

        assertThat(tweetFlux.getStats()).isNotNull();
        assertThat(tweetFlux.getStats().getLikes()).isEqualTo(0L);
        assertThat(tweetFlux.getStats().getReplies()).isEqualTo(0L);
        assertThat(tweetFlux.getStats().getRetweets()).isEqualTo(0L);

        var parent = tweetRepository.findById(tweetFlux.getParent().getUuid()).get();
        assertThat(parent).isNotNull();
        assertThat(parent.getStats()).isNotNull();
        assertThat(parent.getStats().getReplies()).isEqualTo(1L);
    }

    @Test
    void getUserTimelineWithValidUserShouldReturnOk() {
        var tweet = webClient.get()
                .uri("/tweets?username={username}&requester={requester}", user.getUsername(), user.getUsername())
                .exchange()
                .expectStatus().isOk()
                .returnResult(new ParameterizedTypeReference<TweetVO>() {}).getResponseBody()
                .log()
                .next()
                .block();

        assertThat(tweet).isNotNull();
        assertThat(tweet.getUuid()).isNotBlank();
        assertThat(tweet.getContent()).isEqualTo(TweetControllerTest.tweet.getContent());
        assertThat(tweet.getCreatedAt()).isInThePast();
        assertThat(tweet.getMedia()).isNull();
        assertThat(tweet.getParent()).isNull();

        assertThat(tweet.getAuthor()).isNotNull();
        assertThat(tweet.getAuthor().getUuid()).isEqualTo(user.getUuid());
        assertThat(tweet.getAuthor().getUsername()).isEqualTo(user.getUsername());
        assertThat(tweet.getAuthor().getName()).isEqualTo(user.getName());

        assertThat(tweet.getStats()).isNotNull();
        assertThat(tweet.getStats().getLikes()).isEqualTo(TweetControllerTest.tweet.getStats().getLikes());
        assertThat(tweet.getStats().getReplies()).isEqualTo(TweetControllerTest.tweet.getStats().getReplies());
        assertThat(tweet.getStats().getRetweets()).isEqualTo(TweetControllerTest.tweet.getStats().getRetweets());
    }

    @Test
    void getUserTimelineWithInvalidUserShouldReturnNotFound() {
        webClient.get()
                .uri("/tweets?username={username}&requester={requester}", "invalid_user", "invalid_user")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void retweetWithValidUserAndTweetShouldReturnCreated() {
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

        var retweet = RetweetCreateVO.builder()
                .user(user.getUsername())
                .build();

        var newTweet = webClient.post()
                .uri("/tweets/{tweet}/retweets", tweet.getUuid())
                .body(BodyInserters.fromValue(retweet))
                .exchange()
                .expectStatus().isCreated()
                .returnResult(TweetVO.class).getResponseBody()
                .log()
                .next()
                .block();

        assertThat(newTweet).isNotNull();
        assertThat(newTweet.getUuid()).isNotBlank();
        assertThat(newTweet.getContent()).isNullOrEmpty();
        assertThat(newTweet.getCreatedAt()).isInThePast();
        assertThat(newTweet.getMedia()).isNullOrEmpty();
        assertThat(newTweet.getParent()).isNotNull();
        assertThat(newTweet.getType()).isEqualTo(TweetType.RETWEET.name());

        assertThat(newTweet.getAuthor()).isNotNull();
        assertThat(newTweet.getAuthor().getUuid()).isEqualTo(user.getUuid());
        assertThat(newTweet.getAuthor().getUsername()).isEqualTo(user.getUsername());
        assertThat(newTweet.getAuthor().getName()).isEqualTo(user.getName());

        assertThat(newTweet.getStats()).isNotNull();
        assertThat(newTweet.getStats().getLikes()).isEqualTo(0L);
        assertThat(newTweet.getStats().getReplies()).isEqualTo(0L);
        assertThat(newTweet.getStats().getRetweets()).isEqualTo(0L);

        var parent = tweetRepository.findById(newTweet.getParent().getUuid()).get();
        assertThat(parent).isNotNull();
        assertThat(parent.getStats()).isNotNull();
        assertThat(parent.getStats().getRetweets()).isEqualTo(1L);
    }

    @Test
    void retweetWithInvalidUserOrTweetShouldReturnNotFound() {
        var retweet = RetweetCreateVO.builder()
                .user(user.getUsername())
                .build();

        var error = webClient.post()
                .uri("/tweets/{tweet}/retweets", "invalid_tweet")
                .body(BodyInserters.fromValue(retweet))
                .exchange()
                .expectStatus().is4xxClientError()
                .returnResult(ErrorVO.class).getResponseBody()
                .log()
                .next()
                .block();


        assertThat(error).isNotNull();
        assertThat(error.getMessage()).isEqualTo("invalid_tweet not found");
    }
}
