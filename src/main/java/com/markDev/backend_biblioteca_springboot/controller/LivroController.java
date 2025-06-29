package com.markDev.backend_biblioteca_springboot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.markDev.backend_biblioteca_springboot.dto.LivroDTO;
import com.markDev.backend_biblioteca_springboot.service.LivroService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping(value = "/livro")
@CrossOrigin(origins = "http://localhost:3000")
public class LivroController {

	@Autowired
	private LivroService livroService;
	
	@Operation(summary = "Lista todos os livros da estante")
	@GetMapping
	public List<LivroDTO> listarTodos(){
		return livroService.listarTodos();
	}
	
	@Operation(summary = "Insere um novo livro na estante")
	@PostMapping
	public ResponseEntity<Void> inserir(@RequestBody LivroDTO livro) {
	    System.out.println("Recebendo livro: " + livro.getTitulo() + " | Capa: " + 
	        (livro.getCapa() != null ? livro.getCapa().length + " bytes" : "nula"));
	    livroService.inserir(livro);
	    return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@Operation(summary = "Altera um livro existente")
	@PutMapping("/{id}")
	public ResponseEntity<LivroDTO> alterar(@PathVariable Long id, @RequestBody LivroDTO livro) {
	    livro.setId(id); // Garantir que o ID está correto
	    LivroDTO livroAtualizado = livroService.alterar(livro);
	    return ResponseEntity.ok(livroAtualizado);
	    
	}

	@Operation(summary = "Exclui um livro da estante")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluir(@PathVariable("id") Long id){
		livroService.excluir(id);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/buscar")
	public List<LivroDTO> buscarPorTitulo(@RequestParam String titulo) {
	    return livroService.buscarPorTitulo(titulo);
	}

	
}
