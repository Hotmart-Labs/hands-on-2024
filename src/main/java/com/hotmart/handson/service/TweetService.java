package com.hotmart.handson.service;

import com.hotmart.handson.exception.EntityNotFoundException;
import com.hotmart.handson.model.*;
import com.hotmart.handson.repository.LikeRepository;
import com.hotmart.handson.repository.TweetRepository;
import com.hotmart.handson.repository.UserStatsRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class TweetService {

    private final TweetRepository tweetRepository;
    private final UserStatsRepository userStatsRepository;
    private final LikeRepository likeRepository;

    private final UserService userService;
    private final MediaService mediaService;

    private final ModelMapper modelMapper;

    public Optional<Tweet> findById(String tweet) {
        return tweetRepository.findById(tweet);
    }

    public Tweet findByIdOrThrow(String tweet, Function<String, ? extends Throwable> errorSupplier) throws Throwable {
        return findById(tweet)
                .orElseThrow(() -> errorSupplier.apply(tweet));
    }

    /**
     * TODO Challenge #3 - Implement tweet reply
     * @param tweet
     * @param mediaPart
     * @return
     */
    public Tweet create(Tweet tweet, Optional<MultipartFile> media) throws Throwable {
        Optional<String> mediaOptional = mediaService.save(media);

        User u = userService.findByUsernameOrThrow(tweet.getAuthor().getUsername(), EntityNotFoundException::new);
        UserRef user = modelMapper.map(u, UserRef.class);

        tweet.setUuid(UUID.randomUUID().toString());
        tweet.setCreatedAt(new Date());
        tweet.setAuthor(user);
        mediaOptional.ifPresent(tweet::setMedia);

        Tweet savedTweet = tweetRepository.save(tweet);
        userStatsRepository.incTweets(user.getUuid());

        return savedTweet;
    }

    public List<Tweet> getUserTimeline(String username, String requesterUsername) throws Throwable {
        Sort sort = Sort.by(Direction.ASC, "createdAt");

        User user = userService.findByUsernameOrThrow(username, EntityNotFoundException::new);
        User requester = userService.findByUsernameOrThrow(requesterUsername, EntityNotFoundException::new);

        List<Tweet> tweets = tweetRepository.findByAuthorUuid(user.getUuid(), sort);

        for (Tweet tweet : tweets) {
            Optional<Like> like = likeRepository.findByTweetAndUser(tweet.getUuid(), requester.getUuid());
            tweet.setLiked(like.isPresent());
        }

        return tweets;
    }
}
