package com.lima.api.gerenciador.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.lima.api.gerenciador.dto.PersonDTO;
import com.lima.api.gerenciador.exception.ErrorResponse;
import com.lima.api.gerenciador.model.Person;
import com.lima.api.gerenciador.service.PersonService;

@ExtendWith(MockitoExtension.class)
class PersonControllerTest {

    @InjectMocks
    private PersonController personController;

    @Mock
    private PersonService personService;

    @Test
    void createPerson_success() {
        PersonDTO personDTO = new PersonDTO();
        Person person = new Person();
        person.setName("test");
        person.setCpf(45219835145L);

        when(personService.createPerson(any())).thenReturn(person);

        ResponseEntity<Object> response = personController.createPerson(personDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(person, response.getBody());
    }

    @Test
    void createPerson_exception() {
        PersonDTO personDTO = new PersonDTO();

        when(personService.createPerson(any())).thenThrow(new RuntimeException("Error creating person"));

        ResponseEntity<Object> response = personController.createPerson(personDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("Error creating person", errorResponse.getMessage());
    }
    
    @Test
    void getPersonByCpf_exception() {
        Long cpf = 45219835145L;
        String expectedMessage = "Error fetching person by CPF";
        when(personService.getPerson(cpf)).thenThrow(new RuntimeException(expectedMessage));

        ResponseEntity<?> response = personController.getPersonByCpf(cpf);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals(expectedMessage, errorResponse.getMessage());
    }

    @Test
    void updatePersonByCpf_success() {
        PersonDTO personDTO = new PersonDTO();
        Long cpf = 45219835145L;

        ResponseEntity<Object> response = personController.updatePersonByCpf(cpf, personDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Data updated successfully!", response.getBody());
        verify(personService, atLeastOnce()).updatePerson(cpf, personDTO);
    }

    @Test
    void updatePersonByCpf_exception() {
        PersonDTO personDTO = new PersonDTO();
        Long cpf = 45219835145L;

        doThrow(new RuntimeException("Error updating person")).when(personService).updatePerson(cpf, personDTO);

        ResponseEntity<Object> response = personController.updatePersonByCpf(cpf, personDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("Error updating person", errorResponse.getMessage());
    }

    @Test
    void deactivatePersonByCpf_success() {
        Long cpf = 45219835145L;

        ResponseEntity<Object> response = personController.deactivatePersonByCpf(cpf);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Deactivated successfully!", response.getBody());
        verify(personService, atLeastOnce()).deactivatePerson(cpf);
    }

    @Test
    void deactivatePersonByCpf_exception() {
        Long cpf = 45219835145L;

        doThrow(new RuntimeException("Error deactivating person")).when(personService).deactivatePerson(cpf);

        ResponseEntity<Object> response = personController.deactivatePersonByCpf(cpf);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("Error deactivating person", errorResponse.getMessage());
    }

    @Test
    void deletePersonByCpf_success() {
        Long cpf = 45219835145L;

        ResponseEntity<Object> response = personController.deletePersonByCpf(cpf);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Deleted successfully!", response.getBody());
        verify(personService, atLeastOnce()).deletePerson(cpf);
    }

    @Test
    void deletePersonByCpf_exception() {
        Long cpf = 45219835145L;

        doThrow(new RuntimeException("Error deleting person")).when(personService).deletePerson(cpf);

        ResponseEntity<Object> response = personController.deletePersonByCpf(cpf);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("Error deleting person", errorResponse.getMessage());
    }

    @Test
    void listPeople_success() {
        Person person = new Person();
        List<Person> expectedPeople = new ArrayList<>();
        expectedPeople.add(person);
        when(personService.listAllPeople()).thenReturn(expectedPeople);

        ResponseEntity<Object> response = personController.listPeople();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedPeople, response.getBody());
    }

    @Test
    void listPeople_exception() {
        when(personService.listAllPeople()).thenThrow(new RuntimeException("Error listing people"));

        ResponseEntity<Object> response = personController.listPeople();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("Error listing people", errorResponse.getMessage());
    }
}
