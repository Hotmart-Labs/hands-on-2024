package com.hotmart.handson.service;

import com.hotmart.handson.exception.EntityDuplicatedException;
import com.hotmart.handson.exception.EntityNotFoundException;
import com.hotmart.handson.model.Relationship;
import com.hotmart.handson.model.User;
import com.hotmart.handson.repository.RelationshipRepository;
import com.hotmart.handson.repository.UserStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RelationshipService {

    private final RelationshipRepository relationshipRepository;
    private final UserStatsRepository userStatsRepository;

    private final UserService userService;

    public Optional<Relationship> findByUserAndFollower(String user, String follower) {
        Relationship relationship = relationshipRepository.findByUserAndFollower(user, follower);
        return Optional.ofNullable(relationship);
    }

    public Relationship follow(String userUsername, String followerUsername) throws Throwable {
        User user = userService.findByUsernameOrThrow(userUsername, EntityNotFoundException::new);
        User follower = userService.findByUsernameOrThrow(followerUsername, EntityNotFoundException::new);

        String uuid = UUID.randomUUID().toString();
        Relationship relationship = Relationship.builder()
                .uuid(uuid)
                .user(user.getUuid())
                .follower(follower.getUuid())
                .createdAt(new Date())
                .build();

        try {
            relationshipRepository.save(relationship);
        } catch (DuplicateKeyException e) {
            throw new EntityDuplicatedException(String.format("Relationship[%s, %s]", userUsername, followerUsername), e);
        }

        userStatsRepository.incFollowers(user.getUuid());
        userStatsRepository.incFollowing(follower.getUuid());

        return relationship;
    }

    /**
     * TODO Challenge #1 - Bugfix: When an user unfollow another, the counts of followers and following don't update
     *
     * @param userUsername
     * @param followerUsername
     * @return
     */
    public Relationship unfollow(String userUsername, String followerUsername) throws Throwable {
        User user = userService.findByUsernameOrThrow(userUsername, EntityNotFoundException::new);
        User follower = userService.findByUsernameOrThrow(followerUsername, EntityNotFoundException::new);

        return relationshipRepository.deleteByUserAndFollower(user.getUuid(), follower.getUuid());
    }
}
