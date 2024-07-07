package com.lima.api.gerenciador.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lima.api.gerenciador.model.Address;
import com.lima.api.gerenciador.model.Person;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long>{
	
	List<Address> findByPersonAndPrimaryAddressIsTrue(Person pessoa);

}
