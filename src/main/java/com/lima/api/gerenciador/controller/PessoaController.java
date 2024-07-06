package com.lima.api.gerenciador.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

	@PostMapping("/pessoas")
	public ResponseEntity<Object> createPessoa(@RequestBody PessoaDTO pessoa) {
		try {
			Pessoa pessoaSalva = pessoaService.criarPessoa(pessoa);
			return ResponseEntity.status(HttpStatus.CREATED).body(pessoaSalva);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
		}
	}

	@GetMapping("/pessoas/{cpf}")
	public ResponseEntity<?> buscarPessoaPorCpf(@PathVariable("cpf") Long cpf) {
		try {
			Optional<Pessoa> pessoa = pessoaService.consultarPessoa(cpf);
			return ResponseEntity.status(HttpStatus.OK).body(pessoa);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
		}
	}

	@PutMapping("/pessoas/{cpf}")
	public ResponseEntity<Object> updatePessoaPorCpf(@PathVariable("cpf") Long cpf, @RequestBody PessoaDTO update) {
		try {
			pessoaService.atualizarPessoa(cpf, update);
			return ResponseEntity.status(HttpStatus.OK).body("Dados atualizados com sucesso!");
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
		}
	}
	
	@DeleteMapping("/pessoas/desativacao/{cpf}")
	public ResponseEntity<Object> desativaPessoaPorCpf(@PathVariable("cpf") Long cpf) {
		try {
			pessoaService.desativaPessoa(cpf);
			return ResponseEntity.status(HttpStatus.OK).body("Desativado com sucesso!");
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
		}
	}

	@DeleteMapping("/pessoas/{cpf}")
	public ResponseEntity<Object> deletePessoaPorCpf(@PathVariable("cpf") Long cpf) {
		try {
			pessoaService.deletarPessoa(cpf);
			return ResponseEntity.status(HttpStatus.OK).body("Deletado com sucesso!");
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
		}
	}
	
	@GetMapping("/pessoas/lista-pessoas")
	public ResponseEntity<Object> ListaPessoas() {
	    try {
	        List<Pessoa> pessoas = pessoaService.listarTodasPessoas();
	        return ResponseEntity.status(HttpStatus.OK).body(pessoas);
	    } catch (RuntimeException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
	    }
	}
}
