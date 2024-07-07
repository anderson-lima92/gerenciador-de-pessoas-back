package com.lima.api.gerenciador.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.lima.api.gerenciador.dto.PersonDTO;
import com.lima.api.gerenciador.exception.ErrorResponse;
import com.lima.api.gerenciador.model.Person;
import com.lima.api.gerenciador.service.PersonService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AllArgsConstructor
public class PersonController {

    @Autowired
    private PersonService personService;

    @PostMapping("/pessoas")
    public ResponseEntity<Object> createPerson(@RequestBody PersonDTO person) {
        log.info("Request to create person received: {}", person.getName());
        try {
            Person savedPerson = personService.createPerson(person);
            log.info("Person created successfully: {}", savedPerson.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPerson);
        } catch (RuntimeException e) {
            log.error("Error creating person: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/pessoas/{cpf}")
    public ResponseEntity<?> getPersonByCpf(@PathVariable("cpf") Long cpf) {
        log.info("Request to get person by CPF received");
        try {
            Optional<Person> person = personService.getPerson(cpf);
            log.info("Person found name: {}", person.get().getName());
            return ResponseEntity.status(HttpStatus.OK).body(person);
        } catch (RuntimeException e) {
            log.error("Error getting person by CPF: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/pessoas/{cpf}")
    public ResponseEntity<Object> updatePersonByCpf(@PathVariable("cpf") Long cpf, @RequestBody PersonDTO update) {
        log.info("Request to update person received for CPF informed.");
        try {
        	personService.updatePerson(cpf, update);
            log.info("Person updated successfully for CPF informed.");
            return ResponseEntity.status(HttpStatus.OK).body("Data updated successfully!");
        } catch (RuntimeException e) {
            log.error("Error updating person: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/pessoas/desativacao/{cpf}")
    public ResponseEntity<Object> deactivatePersonByCpf(@PathVariable("cpf") Long cpf) {
        log.info("Request to deactivate person received for CPF informed.");
        try {
        	personService.deactivatePerson(cpf);
            log.info("Person deactivated successfully for CPF informed.");
            return ResponseEntity.status(HttpStatus.OK).body("Deactivated successfully!");
        } catch (RuntimeException e) {
            log.error("Error deactivating person: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/pessoas/{cpf}")
    public ResponseEntity<Object> deletePersonByCpf(@PathVariable("cpf") Long cpf) {
        log.info("Request to delete person received for CPF informed.");
        try {
        	personService.deletePerson(cpf);
            log.info("Person deleted successfully for CPF informed.");
            return ResponseEntity.status(HttpStatus.OK).body("Deleted successfully!");
        } catch (RuntimeException e) {
            log.error("Error deleting person: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/pessoas/lista-pessoas")
    public ResponseEntity<Object> listPeople() {
        log.info("Request to list all people received");
        try {
            List<Person> people = personService.listAllPeople();
            log.info("People listed successfully");
            return ResponseEntity.status(HttpStatus.OK).body(people);
        } catch (RuntimeException e) {
            log.error("Error listing people: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }
}
