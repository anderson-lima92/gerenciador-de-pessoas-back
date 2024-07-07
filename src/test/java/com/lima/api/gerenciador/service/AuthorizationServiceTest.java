package com.lima.api.gerenciador.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.lima.api.gerenciador.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthorizationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthorizationService authorizationService;

    @Test
    void loadUserByUsername_UserExists() {
        String username = "testUser";
        UserDetails expectedUser = org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password("password")
                .roles("USER")
                .build();

        when(userRepository.findByLogin(username)).thenReturn(expectedUser);

        UserDetails user = authorizationService.loadUserByUsername(username);

        assertEquals(expectedUser, user);
    }

    @Test
    void loadUserByUsername_UserDoesNotExist() {
        String username = "nonExistentUser";

        when(userRepository.findByLogin(username)).thenReturn(null);

        UsernameNotFoundException thrown = assertThrows(UsernameNotFoundException.class, () -> {
            authorizationService.loadUserByUsername(username);
        });

        assertEquals("User not found: " + username, thrown.getMessage());
    }
}
