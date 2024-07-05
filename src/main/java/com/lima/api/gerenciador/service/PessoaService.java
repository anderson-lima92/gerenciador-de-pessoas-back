package com.lima.api.gerenciador.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lima.api.gerenciador.dto.PessoaDTO;
import com.lima.api.gerenciador.model.Endereco;
import com.lima.api.gerenciador.model.Pessoa;
import com.lima.api.gerenciador.repository.EnderecoRepository;
import com.lima.api.gerenciador.repository.PessoaRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class PessoaService {

	@Autowired
	private PessoaRepository pessoaRepository;

	@Autowired
	private EnderecoRepository enderecoRepository;

	@Autowired
	private HttpServletRequest request;

	public Pessoa criarPessoa(PessoaDTO pessoaDTO) {

		log.info("....................Criando Pessoa....................");

		Pessoa pessoa = new Pessoa();
		pessoa.setNome(pessoaDTO.getNome());
		pessoa.setCpf(pessoaDTO.getCpf());
		pessoa.setDataNascimento(pessoaDTO.getDataNascimento());
		pessoa.setEnderecos(pessoaDTO.getEnderecos());

		try {
			if (pessoaDTO.getEnderecos() != null && !pessoaDTO.getEnderecos().isEmpty()) {
				List<Endereco> enderecos = pessoaDTO.getEnderecos();
				enderecos.get(0).setEnderecoPrincipal(true);

				for (int i = 1; i < enderecos.size(); i++) {
					enderecos.get(i).setEnderecoPrincipal(false);
				}
				pessoa.setEnderecos(enderecos);
			}

			validaCpf(pessoa.getCpf());
			validaEnderecos(pessoa);
			String dataNascimento = validaDataNascimento(pessoa.getDataNascimento());
			pessoa.setDataNascimento(dataNascimento);
			pessoa.setAtivo(true);
			Pessoa pessoaSalva = pessoaRepository.save(pessoa);
			
			return pessoaSalva;

		} catch (Exception e) {
			throw new RuntimeException("Erro ao salvar pessoa: " + e.getMessage());
		}
	}

	private void validaCpf(Long cpf) {
		if (String.valueOf(cpf).length() != 11) {
			throw new IllegalArgumentException("CPF inválido: (" + cpf + ") CPF deve conter 11 dígitos.");
		}
		Optional<Pessoa> pessoaExistente = pessoaRepository.findByCpf(cpf);
		if (!(request.getMethod().equals("PUT") || request.getMethod().equals("GET")
				|| request.getMethod().equals("DELETE"))) {
			if (pessoaExistente.isPresent()) {
				throw new IllegalArgumentException("Já existe uma pessoa com o CPF informado.");
			}
		}

		if (request.getMethod().equals("GET") || request.getMethod().equals("DELETE")) {
			if (!pessoaExistente.isPresent()) {
				throw new IllegalArgumentException("CPF (" + cpf + ") não encontrado.");
			}
		}
	}

	private void validaEnderecos(Pessoa pessoa) {
		boolean temEnderecoPrincipal = false;
		int countEnderecoPrincipal = 0;

		for (Endereco endereco : pessoa.getEnderecos()) {
			endereco.setPessoa(pessoa);
			String cep = endereco.getCep();
			cep = formataCep(cep);
			endereco.setCep(cep);

			if (endereco.isEnderecoPrincipal()) {
				temEnderecoPrincipal = true;
				countEnderecoPrincipal++;
			}

			if (request.getMethod().equals("PUT")) {
				if (endereco.isEnderecoPrincipal()) {
					List<Endereco> enderecosAtivos = enderecoRepository.findByPessoaAndEnderecoPrincipalIsTrue(pessoa);

					for (Endereco enderecoAtivo : enderecosAtivos) {
						enderecoAtivo.setEnderecoPrincipal(false);
						enderecoRepository.save(enderecoAtivo);
					}

					endereco.setEnderecoPrincipal(true);
				}
			}
		}

		if (!temEnderecoPrincipal) {
			throw new IllegalArgumentException("Deve haver pelo menos um endereço principal.");
		} else if (countEnderecoPrincipal > 1) {
			throw new IllegalArgumentException("Só é permitido ter um endereço principal.");
		}
	}

	private String formataCep(String cep) {
		String cepLimpo = cep.replaceAll("[^\\d]", "");
		if (cepLimpo.length() != 8) {
			throw new IllegalArgumentException("Cep inválido: (" + cep + ") Cep deve conter 8 dígitos numéricos.");
		}
		
		return cepLimpo.substring(0, 5) + "-" + cepLimpo.substring(5);
	}

	private String validaDataNascimento(String dataNascimento) {
		String dataNascimentoLimpa = dataNascimento.replaceAll("[^\\d]", "");
		try {
			if (dataNascimentoLimpa.length() < 6) {
				throw new IllegalArgumentException("Data de Nascimento inválida: (" + dataNascimentoLimpa + ") Data deve conter DD-MM-YYYY.");
			}

			dataNascimentoLimpa = formataDataNascimento(dataNascimentoLimpa);

			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			dateFormat.setLenient(false);
			dateFormat.parse(dataNascimentoLimpa);

		} catch (ParseException e) {
			throw new IllegalArgumentException("Data de nascimento inválida: " + e.getMessage());
		}

		return dataNascimentoLimpa;
	}

	private String formataDataNascimento(String dataNascimento) {

		return dataNascimento.substring(0, 2) + "-" + dataNascimento.substring(2, 4) + "-"
				+ dataNascimento.substring(4);
	}
}
