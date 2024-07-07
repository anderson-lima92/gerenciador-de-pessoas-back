package com.lima.api.gerenciador.dto;

import com.lima.api.gerenciador.model.user.UserRole;

public record RegisterDTO(String login, String password, UserRole role) {
}
