package com.lima.api.gerenciador.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.lima.api.gerenciador.model.user.User;
import com.lima.api.gerenciador.model.user.UserRole;
import com.lima.api.gerenciador.repository.UserRepository;

@Configuration
public class InitialDataLoader {
/*
	  Essa função foi desenvolvida apenas para testes locais e desenvolvimento, no intuíto de facilitar o processo de demonstração e testes das funcionalidades
	  da aplicação. 
	  Para um ambiente de produção, essa prática não é recomendada devido a questões de segurança e integridade dos dados.
*/

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner init() {
        return args -> {
            // Verifica se o usuário de teste já existe
            if (userRepository.findByLogin("testuser") == null) {
                // Cria o usuário de teste com a role ADMIN
                User testUser = new User("admin", passwordEncoder.encode("admin"), UserRole.ADMIN);
                userRepository.save(testUser);
            }
        };
    }
}