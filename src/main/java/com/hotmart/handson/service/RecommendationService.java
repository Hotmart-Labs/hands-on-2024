package com.hotmart.handson.service;

import com.hotmart.handson.exception.EntityNotFoundException;
import com.hotmart.handson.model.Relationship;
import com.hotmart.handson.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final UserService userService;
    private final RelationshipService relationshipService;

    public List<User> findLatestUsers(String username) throws Throwable {
        User user = userService.findByUsernameOrThrow(username, EntityNotFoundException::new);

        List<User> users = userService.findAllTail(username);
        for (User u : users) {
            Optional<Relationship> follower = relationshipService.findByUserAndFollower(u.getUuid(), user.getUuid());
            u.setFollowing(follower.isPresent());
        }

        return users;
    }
}
