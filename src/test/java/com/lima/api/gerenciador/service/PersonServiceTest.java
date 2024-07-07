package com.lima.api.gerenciador.service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import com.lima.api.gerenciador.dto.PersonDTO;
import com.lima.api.gerenciador.model.Address;
import com.lima.api.gerenciador.model.Person;
import com.lima.api.gerenciador.repository.AddressRepository;
import com.lima.api.gerenciador.repository.PersonRepository;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    @InjectMocks
    private PersonService personService;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private MockHttpServletRequest request;

    @BeforeEach
    public void setUp() {
        request = new MockHttpServletRequest();
        personService = new PersonService(personRepository, addressRepository, request);
    }

    @Test
    void createPerson_success() {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setName("Test Name");
        personDTO.setCpf(12345678901L);
        personDTO.setBirthDate("01011990");

        Address address = new Address();
        address.setStreet("Rua Teste");
        address.setZipcode("12345678");
        address.setNumber("123");
        address.setCity("Cidade Teste");
        address.setPrimaryAddress(true);

        List<Address> addresses = new ArrayList<>();
        addresses.add(address);
        personDTO.setAddresses(addresses);

        Person person = new Person();
        person.setName("Test Name");
        person.setCpf(12345678901L);
        person.setAddresses(addresses);

        when(personRepository.save(any())).thenReturn(person);

        Person createdPerson = personService.createPerson(personDTO);

        assertEquals("Test Name", createdPerson.getName());
        verify(personRepository, times(1)).save(any());
    }

    @Test
    void createPerson_throwsException() {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setName("Test Name");
        personDTO.setCpf(12345678901L);
        personDTO.setBirthDate("01011990");

        Address address = new Address();
        address.setStreet("Rua Teste");
        address.setZipcode("12345678");
        address.setNumber("123");
        address.setCity("Cidade Teste");
        address.setPrimaryAddress(true);

        List<Address> addresses = new ArrayList<>();
        addresses.add(address);
        personDTO.setAddresses(addresses);

        when(personRepository.save(any())).thenThrow(new RuntimeException("Error saving person"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            personService.createPerson(personDTO);
        });

        assertEquals("Error saving person: Error saving person", thrown.getMessage());
    }

    @Test
    void getPerson_success() {
        Long cpf = 12345678901L;
        Person person = new Person();
        person.setCpf(cpf);
        person.setName("Test Name");

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getMethod()).thenReturn("GET");
        ReflectionTestUtils.setField(personService, "request", mockRequest);

        when(personRepository.findByCpf(cpf)).thenReturn(Optional.of(person));

        Optional<Person> foundPerson = personService.getPerson(cpf);

        assertTrue(foundPerson.isPresent());
        assertEquals("Test Name", foundPerson.get().getName());
        verify(personRepository, atLeastOnce()).findByCpf(cpf);
    }

    @Test
    void getPerson_throwsException() {
        Long cpf = 12345678901L;
        
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getMethod()).thenReturn("GET");
        ReflectionTestUtils.setField(personService, "request", mockRequest);

        when(personRepository.findByCpf(cpf)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            personService.getPerson(cpf);
        });

        assertEquals("Error getting person: CPF not found.", thrown.getMessage());
    }

    @Test
    void updatePerson_success() {
        Long cpf = 12345678901L;
        PersonDTO personDTO = new PersonDTO();
        personDTO.setName("Updated Name");
        personDTO.setBirthDate("02-02-1990");

        Address addressDTO = new Address();
        addressDTO.setStreet("Main St");
        addressDTO.setZipcode("12345-678");
        addressDTO.setNumber("100");
        addressDTO.setCity("City");
        addressDTO.setPrimaryAddress(true);

        List<Address> addressesDTO = new ArrayList<>();
        addressesDTO.add(addressDTO);
        personDTO.setAddresses(addressesDTO);

        Person person = new Person();
        person.setCpf(cpf);
        person.setName("Old Name");

        Address address = new Address();
        address.setStreet("Old St");
        address.setZipcode("54321-876");
        address.setNumber("200");
        address.setCity("Old City");
        address.setPrimaryAddress(true);

        List<Address> addresses = new ArrayList<>();
        addresses.add(address);
        person.setAddresses(addresses);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getMethod()).thenReturn("PUT");
        ReflectionTestUtils.setField(personService, "request", mockRequest);

        when(personRepository.findByCpf(cpf)).thenReturn(Optional.of(person));

        personService.updatePerson(cpf, personDTO);

        verify(personRepository, times(1)).save(person);
        assertEquals("Updated Name", person.getName());
        assertEquals("02-02-1990", person.getBirthDate());
        assertTrue(person.getAddresses().get(0).isPrimaryAddress());
    }

    @Test
    void updatePerson_throwsException() {
        Long cpf = 12345678901L;
        PersonDTO personDTO = new PersonDTO();
        personDTO.setName("Updated Name");
        personDTO.setBirthDate("02021990");

        when(personRepository.findByCpf(cpf)).thenThrow(new RuntimeException("Error updating person"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            personService.updatePerson(cpf, personDTO);
        });

        assertEquals("Error updating person: Error updating person", thrown.getMessage());
    }

    @Test
    void deactivatePerson_success() {
        Long cpf = 12345678901L;
        Person person = new Person();
        person.setCpf(cpf);
        person.setName("Test Name");
        person.setActive(true);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getMethod()).thenReturn("PUT");
        ReflectionTestUtils.setField(personService, "request", mockRequest);

        when(personRepository.findByCpf(cpf)).thenReturn(Optional.of(person));

        personService.deactivatePerson(cpf);

        verify(personRepository, times(1)).save(person);
        assertEquals(false, person.isActive());
    }

    @Test
    void deactivatePerson_throwsException() {
        Long cpf = 12345678901L;
        when(personRepository.findByCpf(cpf)).thenThrow(new RuntimeException("Error deactivating person"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            personService.deactivatePerson(cpf);
        });

        assertEquals("Attention: Error deactivating person", thrown.getMessage());
    }

    @Test
    void deletePerson_success() {
        Long cpf = 12345678901L;
        Person person = new Person();
        person.setCpf(cpf);
        person.setId(1L);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getMethod()).thenReturn("DELETE");
        ReflectionTestUtils.setField(personService, "request", mockRequest);

        when(personRepository.findByCpf(cpf)).thenReturn(Optional.of(person));

        personService.deletePerson(cpf);

        verify(personRepository, times(1)).deleteById(person.getId());
    }

    @Test
    void deletePerson_throwsException() {
        Long cpf = 12345678901L;
        when(personRepository.findByCpf(cpf)).thenThrow(new RuntimeException("Error deleting person"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            personService.deletePerson(cpf);
        });

        assertEquals("Error deleting person: Error deleting person", thrown.getMessage());
    }

    @Test
    void listAllPeople_success() {
        Person person = new Person();
        person.setName("Test Name");
        List<Person> people = new ArrayList<>();
        people.add(person);

        when(personRepository.findAll()).thenReturn(people);

        List<Person> result = personService.listAllPeople();

        assertEquals(1, result.size());
        assertEquals("Test Name", result.get(0).getName());
        verify(personRepository, times(1)).findAll();
    }

    @Test
    void listAllPeople_throwsException() {
        when(personRepository.findAll()).thenThrow(new RuntimeException("No data to list!"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            personService.listAllPeople();
        });

        assertEquals("No data to list!", thrown.getMessage());
    }
}
