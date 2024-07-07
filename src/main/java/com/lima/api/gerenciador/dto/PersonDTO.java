package com.lima.api.gerenciador.dto;

import java.util.List;

import com.lima.api.gerenciador.model.Address;

import lombok.Data;

@Data
public class PersonDTO {
	
	private Long id;
	private String name;
	private Long cpf;
	private String birthDate;
	private List<Address> addresses;
	private boolean active;

}
