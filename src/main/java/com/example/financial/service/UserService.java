package com.example.financial.service;

import org.springframework.stereotype.Service;

import com.example.financial.entity.User;
import com.example.financial.repository.UserRepository;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Find user by username
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElse(null);
    }

    // Save new user in database
    public User saveUser(User user) {
        return userRepository.save(user);
    }
}