package com.hotmart.handson.repository;

public interface UserStatsRepository {

    Boolean incTweets(String user);

    Boolean decTweets(String user);

    Boolean incFollowers(String user);

    Boolean decFollowers(String user);

    Boolean incFollowing(String user);

    Boolean decFollowing(String user);
}
