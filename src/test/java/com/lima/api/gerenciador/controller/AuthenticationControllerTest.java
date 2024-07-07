package com.lima.api.gerenciador.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.lima.api.gerenciador.config.security.TokenService;
import com.lima.api.gerenciador.dto.AuthenticationDTO;
import com.lima.api.gerenciador.dto.LoginResponseDTO;
import com.lima.api.gerenciador.dto.RegisterDTO;
import com.lima.api.gerenciador.model.user.User;
import com.lima.api.gerenciador.model.user.UserRole;
import com.lima.api.gerenciador.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Test
    void login_success() {
        AuthenticationDTO authDto = new AuthenticationDTO("testUser", "password");

        User user = new User();
        user.setLogin("testUser");

        Authentication auth = new UsernamePasswordAuthenticationToken(user, null);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(tokenService.generateToken(any(User.class))).thenReturn("testToken");

        ResponseEntity response = authenticationController.login(authDto);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody() instanceof LoginResponseDTO);
        LoginResponseDTO responseBody = (LoginResponseDTO) response.getBody();
        assertEquals("testToken", responseBody.token());
    }

    @Test
    void login_failure() {
        AuthenticationDTO authDto = new AuthenticationDTO("testUser", "password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Login failed"));

        ResponseEntity response = authenticationController.login(authDto);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Login failed", response.getBody());
    }

    @Test
    void register_success() {
        RegisterDTO registerDto = new RegisterDTO("newUser", "newPassword", UserRole.USER);

        when(userRepository.findByLogin("newUser")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(new User());

        ResponseEntity response = authenticationController.register(registerDto);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void register_userAlreadyExists() {
        RegisterDTO registerDto = new RegisterDTO("existingUser", "newPassword", UserRole.USER);

        when(userRepository.findByLogin("existingUser")).thenReturn(new User());

        ResponseEntity response = authenticationController.register(registerDto);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("User already exists", response.getBody());
    }
}
