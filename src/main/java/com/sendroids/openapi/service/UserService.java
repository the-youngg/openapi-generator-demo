package com.sendroids.openapi.service;

import com.sendroids.openapi.domain.User;
import com.sendroids.openapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getUserById(
            final Long id
    ) {
        return userRepository.findById(id);
    }

    public User save(
            final User user
    ) {
        return userRepository.save(user);
    }
}
