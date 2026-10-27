package com.example.financial.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.financial.dto.LoginRequest;
import com.example.financial.entity.User;
import com.example.financial.service.JwtService;
import com.example.financial.service.UserService;

@RestController
public class AuthController {

    private UserService userService;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;

    public AuthController(UserService userService,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // Register new user
    @PostMapping("/register")
    public String register(@RequestBody User user) {

        try {

            // Check if username already exists
            if (userService.findByUsername(user.getUsername()) != null) {
                return "Username already exists";
            }

            // Encrypt password before saving
            user.setPassword(
                    passwordEncoder.encode(user.getPassword())
            );

            // New users will have USER role
            user.setRole("USER");

            // Save user in database
            userService.saveUser(user);

            return "User registered successfully";

        } catch (Exception e) {

            return "Registration failed: " + e.getMessage();
        }
    }

    // Login existing user
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {

        try {

            User user = userService.findByUsername(request.getUsername());

            if (user == null) {
                return "User not found";
            }

            if (!passwordEncoder.matches(
                    request.getPassword(),
                    user.getPassword())) {
                return "Wrong password";
            }

            String token = jwtService.generateToken(
                    user.getUsername(),
                    user.getRole()
            );

            return token;

        } catch (Exception e) {

            return "Login failed: " + e.getMessage();
        }
    }
}