package com.lima.api.gerenciador.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lima.api.gerenciador.dto.PersonDTO;
import com.lima.api.gerenciador.model.Address;
import com.lima.api.gerenciador.model.Person;
import com.lima.api.gerenciador.repository.AddressRepository;
import com.lima.api.gerenciador.repository.PersonRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class PersonService {

    @Autowired
    private PersonRepository pessoaRepository;

    @Autowired
    private AddressRepository enderecoRepository;

    @Autowired
    private HttpServletRequest request;

    public Person createPerson(PersonDTO pessoaDTO) {
        log.info("Creating new person: {}", pessoaDTO.getName());
        Person person = new Person();
        person.setName(pessoaDTO.getName());
        person.setCpf(pessoaDTO.getCpf());
        person.setBirthDate(pessoaDTO.getBirthDate());
        person.setAddresses(pessoaDTO.getAddresses());

        try {
            if (pessoaDTO.getAddresses() != null && !pessoaDTO.getAddresses().isEmpty()) {
                List<Address> addresses = pessoaDTO.getAddresses();
                addresses.get(0).setPrimaryAddress(true);

                for (int i = 1; i < addresses.size(); i++) {
                    addresses.get(i).setPrimaryAddress(false);
                }

                person.setAddresses(addresses);
            }

            validateCpf(person.getCpf());
            validateAddresses(person);

            String birthDate = validateBirthDate(person.getBirthDate());
            person.setBirthDate(birthDate);
            person.setActive(true);

            Person savedPerson = pessoaRepository.save(person);
            log.info("Person created and saved successfully: {}", savedPerson.getName());
            return savedPerson;

        } catch (Exception e) {
            log.error("Error saving person: {}", e.getMessage());
            throw new RuntimeException("Error saving person: " + e.getMessage());
        }
    }

    public Optional<Person> getPerson(Long cpf) {
        log.info("Getting person with provided CPF");
        try {
            validateCpf(cpf);
            Optional<Person> foundPerson = pessoaRepository.findByCpf(cpf);
            log.info("Person found: {}", foundPerson.get().getName());
            return foundPerson;
        } catch (Exception e) {
            log.error("Error getting person: {}", e.getMessage());
            throw new RuntimeException("Error getting person: " + e.getMessage());
        }
    }

    public void updatePerson(Long cpf, PersonDTO update) {
        log.info("Updating person with provided CPF");
        try {
            validateCpf(cpf);
            Optional<Person> foundPerson = pessoaRepository.findByCpf(cpf);

            if (foundPerson.isPresent()) {
                Person person = foundPerson.get();
                person.setName(update.getName());
                String birthDate = validateBirthDate(update.getBirthDate());
                person.setBirthDate(birthDate);
                person.setAddresses(update.getAddresses());
                validateAddresses(person);
                person.setActive(true);
                pessoaRepository.save(person);
                log.info("Person updated successfully: {}", person.getName());
            } else {
                throw new IllegalArgumentException("CPF not found.");
            }
        } catch (Exception e) {
            log.error("Error updating person: {}", e.getMessage());
            throw new RuntimeException("Error updating person: " + e.getMessage());
        }
    }

    public void deactivatePerson(Long cpf) {
        log.info("Deactivating person with provided CPF");
        try {
            validateCpf(cpf);
            Optional<Person> foundPerson = pessoaRepository.findByCpf(cpf);
            if (foundPerson.isPresent()) {
                Person person = foundPerson.get();
                if (!person.isActive()) {
                    throw new RuntimeException("Person is already deactivated");
                }
                person.setActive(false);
                pessoaRepository.save(person);
                log.info("Person deactivated successfully: {}", person.getName());
            }
        } catch (Exception e) {
            log.error("Error deactivating person: {}", e.getMessage());
            throw new IllegalArgumentException("Attention: " + e.getMessage());
        }
    }

    public void deletePerson(Long cpf) {
        log.info("Deleting person with provided CPF");
        try {
            validateCpf(cpf);
            Optional<Person> foundPerson = pessoaRepository.findByCpf(cpf);
            if (foundPerson.isPresent()) {
                pessoaRepository.deleteById(foundPerson.get().getId());
                log.info("Person deleted successfully.");
            }
        } catch (Exception e) {
            log.error("Error deleting person: {}", e.getMessage());
            throw new IllegalArgumentException("Error deleting person: " + e.getMessage());
        }
    }

    public List<Person> listAllPeople() {
        log.info("Listing all people");
        List<Person> people = pessoaRepository.findAll();
        if (people.isEmpty()) {
            throw new RuntimeException("No data to list!");
        }
        log.info("People listed successfully");
        return people;
    }

    private void validateCpf(Long cpf) {
        if (String.valueOf(cpf).length() != 11) {
            throw new IllegalArgumentException("Invalid CPF, CPF must be 11 digits.");
        }
        Optional<Person> existingPerson = pessoaRepository.findByCpf(cpf);
        if (!(request.getMethod().equals("PUT") || request.getMethod().equals("GET")
                || request.getMethod().equals("DELETE"))) {
            if (existingPerson.isPresent()) {
                throw new IllegalArgumentException("A person with the provided CPF already exists.");
            }
        }
        if (request.getMethod().equals("GET") || request.getMethod().equals("DELETE")) {
            if (!existingPerson.isPresent()) {
                throw new IllegalArgumentException("CPF not found.");
            }
        }
    }

    private void validateAddresses(Person person) {
        boolean hasMainAddress = false;
        int mainAddressCount = 0;
        for (Address address : person.getAddresses()) {
            address.setPerson(person);
            String cep = address.getZipcode();
            cep = formatCep(cep);
            address.setZipcode(cep);
            if (address.isPrimaryAddress()) {
                hasMainAddress = true;
                mainAddressCount++;
            }
            if (request.getMethod().equals("PUT")) {
                if (address.isPrimaryAddress()) {
                    List<Address> activeAddresses = enderecoRepository.findByPersonAndPrimaryAddressIsTrue(person);
                    for (Address activeAddress : activeAddresses) {
                        activeAddress.setPrimaryAddress(false);
                        enderecoRepository.save(activeAddress);
                    }
                    address.setPrimaryAddress(true);
                }
            }
        }
        if (!hasMainAddress) {
            throw new IllegalArgumentException("There must be at least one main address.");
        } else if (mainAddressCount > 1) {
            throw new IllegalArgumentException("Only one main address is allowed.");
        }
    }

    private String formatCep(String cep) {
        String cleanCep = cep.replaceAll("[^\\d]", "");
        if (cleanCep.length() != 8) {
            throw new IllegalArgumentException("Invalid CEP: (" + cep + ") CEP must be 8 numeric digits.");
        }
        return cleanCep.substring(0, 5) + "-" + cleanCep.substring(5);
    }

    private String validateBirthDate(String birthDate) {
        String cleanBirthDate = birthDate.replaceAll("[^\\d]", "");
        try {
            if (cleanBirthDate.length() < 6) {
                throw new IllegalArgumentException("Invalid birth date: (" + cleanBirthDate + ") Date must be DD-MM-YYYY.");
            }
            cleanBirthDate = formatBirthDate(cleanBirthDate);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            dateFormat.setLenient(false);
            dateFormat.parse(cleanBirthDate);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid birth date: " + e.getMessage());
        }
        return cleanBirthDate;
    }

    private String formatBirthDate(String birthDate) {
        return birthDate.substring(0, 2) + "-" + birthDate.substring(2, 4) + "-" + birthDate.substring(4);
    }
}
