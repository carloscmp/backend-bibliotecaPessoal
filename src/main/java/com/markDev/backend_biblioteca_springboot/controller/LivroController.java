package com.markDev.backend_biblioteca_springboot.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.markDev.backend_biblioteca_springboot.dto.BookSearchResultDTO; // <<< NOVO IMPORT
import com.markDev.backend_biblioteca_springboot.dto.LivroDTO;
import com.markDev.backend_biblioteca_springboot.entity.LivroEntity;
import com.markDev.backend_biblioteca_springboot.service.GoogleBooksService;
import com.markDev.backend_biblioteca_springboot.service.LivroService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/api/livros")
@CrossOrigin(origins = "*")
public class LivroController {

	private final LivroService livroService;
	private final GoogleBooksService googleBooksService;

	public LivroController(LivroService livroService, GoogleBooksService googleBooksService) {
		this.livroService = livroService;
		this.googleBooksService = googleBooksService;
	}

	@Operation(summary = "Lista todos os livros da estante")
	@GetMapping
	public ResponseEntity<List<LivroDTO>> listarTodos() {
		List<LivroDTO> livros = livroService.listarTodos();
		return ResponseEntity.ok(livros);
	}

	@Operation(summary = "Insere um novo livro na estante")
	@PostMapping
	public ResponseEntity<LivroDTO> inserir(@Valid @RequestBody LivroDTO livroDTO) {
		LivroEntity livroSalvo = livroService.inserir(livroDTO);
		LivroDTO dtoDeRetorno = new LivroDTO(livroSalvo);
		return ResponseEntity.status(HttpStatus.CREATED).body(dtoDeRetorno);
	}

	@Operation(summary = "Altera um livro existente")
	@PutMapping("/{id}")
	public ResponseEntity<LivroDTO> alterar(@PathVariable Long id, @Valid @RequestBody LivroDTO livroDTO) {
		LivroEntity livroAtualizado = livroService.alterar(id, livroDTO);
		LivroDTO dtoDeRetorno = new LivroDTO(livroAtualizado);
		return ResponseEntity.ok(dtoDeRetorno);
	}

	@Operation(summary = "Exclui um livro da estante")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluir(@PathVariable("id") Long id) {
		livroService.excluir(id);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Busca livros por título (na base de dados local)")
	@GetMapping("/buscar")
	public ResponseEntity<List<LivroDTO>> buscarPorTitulo(@RequestParam String titulo) {
		List<LivroDTO> livros = livroService.buscarPorTitulo(titulo);
		return ResponseEntity.ok(livros);
	}

	/**
	 * <<< MÉTODO ALTERADO >>> O tipo de retorno agora é uma lista de DTOs de busca,
	 * e não mais uma String. O Spring irá automaticamente converter esta lista para
	 * um JSON.
	 */
	@Operation(summary = "Busca livros em uma API externa de forma segura")
	@GetMapping("/busca-externa")
	public ResponseEntity<List<BookSearchResultDTO>> buscarExterno(@RequestParam(required = false) String titulo,
			@RequestParam(required = false) String autor) {

		List<BookSearchResultDTO> resultados = googleBooksService.buscarLivrosExterno(titulo, autor);
		return ResponseEntity.ok(resultados);
	}
}
