package com.hotmart.handson.service;

import com.hotmart.handson.exception.EntityNotFoundException;
import com.hotmart.handson.model.*;
import com.hotmart.handson.repository.TweetRepository;
import com.hotmart.handson.repository.TweetStatsRepository;
import com.hotmart.handson.repository.UserStatsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TweetServiceTest {

    @Mock
    private TweetRepository tweetRepository;

    @Mock
    private UserService userService;

    @Mock
    private MediaService mediaService;

    @Mock
    private UserStatsRepository userStatsRepository;

    @Mock
    private TweetStatsRepository tweetStatsRepository;

    @InjectMocks
    private TweetService tweetService;

    @Spy
    private final ModelMapper modelMapper = new ModelMapper();

    private final User author = User.builder()
            .uuid(UUID.randomUUID().toString())
            .username("author")
            .name("Author")
            .build();

    @Test
    void createShouldCreateAndReturnTweet() throws Throwable {
        lenient().when(mediaService.save(any())).thenReturn(Optional.of("image.jpg"));
        lenient().when(userStatsRepository.incTweets(any())).thenReturn(true);
        lenient().when(userService.findByUsernameOrThrow(any(), any())).thenReturn(author);
        lenient().when(tweetRepository.save(any())).thenAnswer(ctx -> ctx.getArgument(0, Tweet.class));

        var tweet = Tweet.builder()
                .content("content")
                .author(UserRef.builder()
                        .uuid(author.getUuid())
                        .username(author.getUsername())
                        .name(author.getName())
                        .build())
                .build();

        var t = tweetService.create(tweet, Optional.empty());

        assertThat(t).isNotNull();
        assertThat(t.getUuid()).isNotNull();
        assertThat(t.getContent()).isEqualTo("content");
        assertThat(t.getType()).isEqualTo(TweetType.TWEET);
       // assertThat(t.getCreatedAt()).isInThePast();
        assertThat(t.getMedia()).isEqualTo("image.jpg");
        assertThat(t.getAuthor()).isNotNull();
        assertThat(t.getAuthor().getUuid()).isEqualTo(author.getUuid());
        assertThat(t.getParent()).isNull();

        verify(tweetRepository).save(any());
        verify(userStatsRepository).incTweets(any());
    }

    @Test
    void createWithParentShouldCreateAndReturnTweetReply() throws Throwable {
        lenient().when(mediaService.save(any())).thenReturn(Optional.empty());
        lenient().when(userStatsRepository.incTweets(any())).thenReturn(true);
        lenient().when(tweetStatsRepository.incReplies(any())).thenReturn(true);
        lenient().when(userService.findByUsernameOrThrow(any(), any())).thenReturn(author);
        lenient().when(tweetRepository.save(any())).thenAnswer(ctx -> ctx.getArgument(0, Tweet.class));

        var tweet = Tweet.builder()
                .uuid(UUID.randomUUID().toString())
                .content("content")
                .author(UserRef.builder()
                        .uuid(author.getUuid())
                        .username(author.getUsername())
                        .name(author.getName())
                        .build())
                .build();
        when(tweetRepository.findById(anyString())).thenReturn(Optional.of(tweet));

        var reply = Tweet.builder()
                .content("reply")
                .parent(TweetRef.builder()
                        .uuid(tweet.getUuid())
                        .build())
                .author(UserRef.builder()
                        .uuid(author.getUuid())
                        .username(author.getUsername())
                        .name(author.getName())
                        .build())
                .build();

        var t = tweetService.create(reply, Optional.empty());
        assertThat(t).isNotNull();
        assertThat(t.getUuid()).isNotNull();
        assertThat(t.getContent()).isEqualTo("reply");
        assertThat(t.getType()).isEqualTo(TweetType.REPLY);
       // assertThat(t.getCreatedAt()).isInThePast();
        assertThat(t.getAuthor()).isNotNull();
        assertThat(t.getAuthor().getUuid()).isEqualTo(author.getUuid());
        assertThat(t.getParent()).isNotNull();
        assertThat(t.getParent().getUuid()).isEqualTo(tweet.getUuid());

        verify(tweetRepository).save(any());
        verify(userStatsRepository).incTweets(any());
        verify(tweetStatsRepository).incReplies(any());
    }

    @Test
    void createWithInvalidUserShouldThrowsEntityNotFoundException() throws Throwable {
        lenient().when(mediaService.save(any())).thenReturn(Optional.empty());
        lenient().when(userService.findByUsernameOrThrow(any(), any())).thenThrow(new EntityNotFoundException(author.getUsername()));

        var tweet = Tweet.builder()
                .content("content")
                .author(UserRef.builder()
                        .uuid(author.getUuid())
                        .username(author.getUsername())
                        .name(author.getName())
                        .build())
                .build();


        assertThatThrownBy(() -> tweetService.create(tweet, Optional.empty())).isInstanceOf(EntityNotFoundException.class);

        verify(tweetRepository, never()).save(any());
        verify(userStatsRepository, never()).incTweets(any());
    }
}
