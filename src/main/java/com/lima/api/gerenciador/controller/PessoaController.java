package com.lima.api.gerenciador.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.lima.api.gerenciador.dto.PessoaDTO;
import com.lima.api.gerenciador.exception.ErrorResponse;
import com.lima.api.gerenciador.model.Pessoa;
import com.lima.api.gerenciador.service.PessoaService;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class PessoaController {

	@Autowired
	private PessoaService pessoaService;
	
	@GetMapping
	public String home() {
	    return "index";
	}

	@PostMapping("/pessoas")
	public ResponseEntity<Object> createPessoa(@RequestBody PessoaDTO pessoa) {
		try {
			Pessoa pessoaSalva = pessoaService.criarPessoa(pessoa);
			return ResponseEntity.status(HttpStatus.CREATED).body(pessoaSalva);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
		}
	}

}
