package com.markDev.backend_biblioteca_springboot.service;

import java.util.List;
import org.springframework.beans.BeanUtils;
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

	public List<LivroDTO> listarTodos() {
		List<LivroEntity> livros = livroRepository.findAll();
		// --- ESTA É A PARTE CORRIGIDA ---
		// Em vez de .map(LivroDTO::new), fazemos a conversão aqui.
		return livros.stream().map(entidade -> {
			LivroDTO dto = new LivroDTO();
			BeanUtils.copyProperties(entidade, dto); // Copia os dados da entidade para o DTO
			return dto;
		}).toList();
	}

	public void inserir(LivroDTO livro) {
		LivroEntity livroEntity = new LivroEntity(livro);
		livroRepository.save(livroEntity);
	}

	public LivroDTO alterar(LivroDTO livro) {
		LivroEntity livroEntity = new LivroEntity(livro);
		// Também corrigindo aqui para retornar um DTO da forma segura
		LivroEntity entidadeSalva = livroRepository.save(livroEntity);
		LivroDTO dtoDeRetorno = new LivroDTO();
		BeanUtils.copyProperties(entidadeSalva, dtoDeRetorno);
		return dtoDeRetorno;
	}

	public void excluir(Long id) {
		LivroEntity livro = livroRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Livro com ID " + id + " não encontrado."));
		livroRepository.delete(livro);
	}

	public List<LivroDTO> buscarPorTitulo(String titulo) {
		List<LivroEntity> livros = livroRepository.findByTituloContainingIgnoreCase(titulo);
		// --- E CORRIGINDO AQUI TAMBÉM ---
		return livros.stream().map(entidade -> {
			LivroDTO dto = new LivroDTO();
			BeanUtils.copyProperties(entidade, dto);
			return dto;
		}).toList();
	}
}