package com.markDev.backend_biblioteca_springboot.controller;

import com.markDev.backend_biblioteca_springboot.dto.LivroDTO;
import com.markDev.backend_biblioteca_springboot.entity.LivroEntity;
import com.markDev.backend_biblioteca_springboot.service.GoogleBooksService;
import com.markDev.backend_biblioteca_springboot.service.LivroService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
// <<< CORREÇÃO PRINCIPAL AQUI: Removido o prefixo "/api" para corresponder ao frontend >>>
@RequestMapping(value = "/livros") 
@CrossOrigin(origins = "http://localhost:3000")
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

	@Operation(summary = "Busca livros em uma API externa de forma segura")
	@GetMapping("/busca-externa")
	public ResponseEntity<String> buscarExterno(@RequestParam String titulo) {
		String resultadoJson = googleBooksService.buscarLivrosExterno(titulo);
		return ResponseEntity.ok(resultadoJson);
	}
}
