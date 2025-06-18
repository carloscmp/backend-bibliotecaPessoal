package com.markDev.backend_biblioteca_springboot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.markDev.backend_biblioteca_springboot.dto.LivroDTO;
import com.markDev.backend_biblioteca_springboot.entity.LivroEntity;
import com.markDev.backend_biblioteca_springboot.exception.RecursoNaoEncontradoException;
import com.markDev.backend_biblioteca_springboot.repository.LivroRepository;

@Service
public class LivroService {
	
	@Autowired
	private LivroRepository livroRepository;
	
	public List<LivroDTO> listarTodos(){
		List<LivroEntity> livros = livroRepository.findAll();
		return livros.stream().map(LivroDTO::new).toList();
	}
	
	public void inserir(LivroDTO livro) {
		LivroEntity livroEntity = new LivroEntity(livro);
		livroRepository.save(livroEntity);
	}
	
	public LivroDTO alterar(LivroDTO livro) {
		LivroEntity livroEntity = new LivroEntity(livro);
		return new LivroDTO(livroRepository.save(livroEntity));
	}
	
	public void excluir(Long id) {
		LivroEntity livro = livroRepository.findById(id)
		        .orElseThrow(() -> new RecursoNaoEncontradoException("Livro com ID " + id + " não encontrado."));
		livroRepository.delete(livro);
	}
	
	public List<LivroDTO> buscarPorTitulo(String titulo) {
	    List<LivroEntity> livros = livroRepository.findByTituloContainingIgnoreCase(titulo);
	    return livros.stream().map(LivroDTO::new).toList();
	}

	
}
