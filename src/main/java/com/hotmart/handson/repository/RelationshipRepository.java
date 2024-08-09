package com.hotmart.handson.repository;

import com.hotmart.handson.model.Relationship;
import org.springframework.data.repository.CrudRepository;

public interface RelationshipRepository extends CrudRepository<Relationship, String> {

    Relationship findByUserAndFollower(String user, String follower);

    Relationship deleteByUserAndFollower(String user, String follower);
}
