package com.vinayak.ecommerce.service;

import com.vinayak.ecommerce.model.LoginRequest;
import com.vinayak.ecommerce.model.User;
import com.vinayak.ecommerce.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User register(User user){

        if(userRepository.existsByUsername(user.getUsername())){
            throw new IllegalArgumentException("Username already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");

        return userRepository.save(user);
    }

    public Map<String, String> login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new IllegalArgumentException("Invalid username or password");
        }

        String token = jwtService.generateToken(user.getUsername());

        return Map.of(
                "token", token,
                "token_type", "Bearer"
        );

    }
}
