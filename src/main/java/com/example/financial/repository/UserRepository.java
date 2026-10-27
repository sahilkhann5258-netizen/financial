package com.example.financial.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.financial.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by username
    Optional<User> findByUsername(String username);

   
}