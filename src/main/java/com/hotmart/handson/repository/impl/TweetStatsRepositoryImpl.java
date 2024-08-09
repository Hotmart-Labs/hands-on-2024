package com.hotmart.handson.repository.impl;

import com.hotmart.handson.model.Tweet;
import com.hotmart.handson.repository.TweetStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

@Repository
public class TweetStatsRepositoryImpl extends AbstractStatsRepository implements TweetStatsRepository {

    @Autowired
    public TweetStatsRepositoryImpl(MongoOperations mongoOperations) {
        super(mongoOperations);
    }

    public Boolean incLikes(String tweet) {
        return inc(tweet, "stats.likes", Tweet.class);
    }

    public Boolean decLikes(String tweet) {
        return dec(tweet, "stats.likes", Tweet.class);
    }

    public Boolean incReplies(String tweet) {
        return inc(tweet, "stats.replies", Tweet.class);
    }

    public Boolean decReplies(String tweet) {
        return dec(tweet, "stats.replies", Tweet.class);
    }

    public Boolean incRetweets(String tweet) {
        return inc(tweet, "stats.retweets", Tweet.class);
    }

    public Boolean decRetweets(String tweet) {
        return dec(tweet, "stats.retweets", Tweet.class);
    }
}
