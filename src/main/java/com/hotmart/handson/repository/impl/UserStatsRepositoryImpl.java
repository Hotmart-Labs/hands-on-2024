package com.hotmart.handson.repository.impl;

import com.hotmart.handson.model.User;
import com.hotmart.handson.repository.UserStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

@Repository
public class UserStatsRepositoryImpl extends AbstractStatsRepository implements UserStatsRepository {

    @Autowired
    public UserStatsRepositoryImpl(MongoOperations mongoOperations) {
        super(mongoOperations);
    }

    public Boolean incTweets(String user) {
        return inc(user, "stats.tweets", User.class);
    }

    public Boolean decTweets(String user) {
        return dec(user, "stats.tweets", User.class);
    }

    public Boolean incFollowers(String user) {
        return inc(user, "stats.followers", User.class);
    }

    public Boolean decFollowers(String user) {
        return dec(user, "stats.followers", User.class);
    }

    public Boolean incFollowing(String user) {
        return inc(user, "stats.following", User.class);
    }

    public Boolean decFollowing(String user) {
        return dec(user, "stats.following", User.class);
    }
}
