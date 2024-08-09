package com.hotmart.handson.service;

import com.hotmart.handson.exception.EntityDuplicatedException;
import com.hotmart.handson.exception.EntityNotFoundException;
import com.hotmart.handson.model.Like;
import com.hotmart.handson.model.Tweet;
import com.hotmart.handson.model.User;
import com.hotmart.handson.repository.LikeRepository;
import com.hotmart.handson.repository.TweetStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final TweetStatsRepository tweetStatsRepository;

    private final TweetService tweetService;
    private final UserService userService;

    @Transactional
    public Like like(String tweetId, String username) throws Throwable {
        Tweet tweet = tweetService.findByIdOrThrow(tweetId, EntityNotFoundException::new);
        User user = userService.findByUsernameOrThrow(username, EntityNotFoundException::new);

        Like like = Like.builder()
                .user(user.getUuid())
                .tweet(tweet.getUuid())
                .createdAt(new Date())
                .build();

        try {
            likeRepository.save(like);
        } catch (DuplicateKeyException e) {
            throw new EntityDuplicatedException(String.format("Like[%s, %s]", tweetId, username), e);
        }

        tweetStatsRepository.incLikes(tweetId);
        return like;
    }

    @Transactional
    public Like dislike(String tweetId, String username) throws Throwable {
        return null;
    }
}
