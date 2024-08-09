package com.hotmart.handson.repository;

import com.hotmart.handson.model.User;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, String> {

    Optional<User> findByUsername(String username);

    @Tailable
    List<User> findByUsernameNot(String username);
}
