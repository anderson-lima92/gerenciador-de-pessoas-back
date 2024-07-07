package com.lima.api.gerenciador.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lima.api.gerenciador.config.security.TokenService;
import com.lima.api.gerenciador.dto.AuthenticationDTO;
import com.lima.api.gerenciador.dto.LoginResponseDTO;
import com.lima.api.gerenciador.dto.RegisterDTO;
import com.lima.api.gerenciador.model.user.User;
import com.lima.api.gerenciador.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository repository;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data){
        log.info("Attempting login for user: {}", data.login());
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        try {
            var auth = this.authenticationManager.authenticate(usernamePassword);
            var token = tokenService.generateToken((User) auth.getPrincipal());
            log.info("Login successful for user: {}", data.login());
            return ResponseEntity.ok(new LoginResponseDTO(token));
        } catch (Exception e) {
            log.error("Login failed for user: {}", data.login(), e);
            return ResponseEntity.badRequest().body("Login failed");
        }
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO data){
        log.info("Attempting to register user: {}", data.login());
        if(this.repository.findByLogin(data.login()) != null) {
            log.warn("User already exists: {}", data.login());
            return ResponseEntity.badRequest().body("User already exists");
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser = new User(data.login(), encryptedPassword, data.role());

        this.repository.save(newUser);
        log.info("User registered successfully: {}", data.login());
        return ResponseEntity.ok().build();
    }
}