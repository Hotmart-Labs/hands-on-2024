package com.hotmart.handson.repository;

import com.hotmart.handson.model.Like;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LikeRepository extends CrudRepository<Like, String> {

    Optional<Like> findByTweetAndUser(String tweet, String user);

    Like deleteByTweetAndUser(String tweet, String user);
}
