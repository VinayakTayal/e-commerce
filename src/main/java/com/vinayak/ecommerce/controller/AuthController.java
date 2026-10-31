package com.vinayak.ecommerce.controller;

import com.vinayak.ecommerce.model.LoginRequest;
import com.vinayak.ecommerce.model.User;
import com.vinayak.ecommerce.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        User registeredUser = authService.register(user);

        Map<String, Object> response = Map.of(
                "message", "User registered successfully",
                "id",registeredUser.getId(),
                "username",registeredUser.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }


}
