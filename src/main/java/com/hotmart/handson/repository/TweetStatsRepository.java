package com.hotmart.handson.repository;

public interface TweetStatsRepository {

    Boolean incLikes(String tweet);

    Boolean decLikes(String tweet);

    Boolean incReplies(String tweet);

    Boolean decReplies(String tweet);

    Boolean incRetweets(String tweet);

    Boolean decRetweets(String tweet);
}
