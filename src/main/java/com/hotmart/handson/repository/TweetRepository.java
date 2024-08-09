package com.hotmart.handson.repository;

import com.hotmart.handson.model.Tweet;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TweetRepository extends CrudRepository<Tweet, String> {

    List<Tweet> findByAuthorUuid(String author, Sort sort);
}
