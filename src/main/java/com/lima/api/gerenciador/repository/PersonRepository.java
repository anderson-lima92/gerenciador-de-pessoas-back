package com.lima.api.gerenciador.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.lima.api.gerenciador.model.Person;

import jakarta.transaction.Transactional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
	Optional<Person> findByCpf(Long cpf);

	Person findPrimaryAddressByCpf(Long cpf);
	
    @Modifying
    @Transactional
    void deleteById(Long id);
}
