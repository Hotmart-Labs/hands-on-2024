package com.hotmart.handson.service;

import com.hotmart.handson.exception.EntityDuplicatedException;
import com.hotmart.handson.model.User;
import com.hotmart.handson.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findByUsernameOrThrow(String username, Function<String, ? extends Throwable> errorSupplier) throws Throwable {
        return findByUsername(username)
                .orElseThrow(() -> errorSupplier.apply(username));
    }

    public List<User> findAllTail(String username) {
        return userRepository.findByUsernameNot(username);
    }

    public User create(User user) {
        user.setUuid(UUID.randomUUID().toString());
        user.setJoinedAt(new Date());
        try {
            return userRepository.save(user);
        } catch (DuplicateKeyException e) {
            throw new EntityDuplicatedException(String.format("User %s", user.getUsername()), e);
        }
    }
}
