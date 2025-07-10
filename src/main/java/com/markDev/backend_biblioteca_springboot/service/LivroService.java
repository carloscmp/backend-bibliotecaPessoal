package com.markDev.backend_biblioteca_springboot.service;

import com.markDev.backend_biblioteca_springboot.dto.LivroDTO;
import com.markDev.backend_biblioteca_springboot.entity.LivroEntity;
import com.markDev.backend_biblioteca_springboot.exception.RecursoNaoEncontradoException;
import com.markDev.backend_biblioteca_springboot.repository.LivroRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LivroService {

    @Autowired
    private LivroRepository livroRepository;

    /**
     * Lista todos os livros e os converte para DTOs.
     */
    @Transactional(readOnly = true)
    public List<LivroDTO> listarTodos() {
        List<LivroEntity> livros = livroRepository.findAll();
        // Mapeia cada entidade para um DTO de forma explícita
        return livros.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Insere um novo livro no banco de dados.
     * @param livroDTO Os dados do novo livro vindos do frontend.
     * @return A Entidade do livro que foi salva, contendo o novo ID gerado.
     */
    @Transactional
    public LivroEntity inserir(LivroDTO livroDTO) {
        LivroEntity livroEntity = new LivroEntity();
        // Converte o DTO recebido em uma nova Entidade
        BeanUtils.copyProperties(livroDTO, livroEntity);
        // Salva a nova entidade e a retorna
        return livroRepository.save(livroEntity);
    }

    /**
     * Altera um livro existente no banco de dados.
     * @param id O ID do livro a ser alterado.
     * @param livroDTO Os novos dados para o livro.
     * @return A Entidade do livro que foi atualizada.
     */
    @Transactional
    public LivroEntity alterar(Long id, LivroDTO livroDTO) {
        // Busca o livro existente no banco de dados
        LivroEntity livroEntity = livroRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro com ID " + id + " não encontrado para alteração."));

        // Copia as propriedades do DTO para a entidade encontrada, ignorando o ID
        BeanUtils.copyProperties(livroDTO, livroEntity, "id");
        
        // Salva a entidade atualizada e a retorna
        return livroRepository.save(livroEntity);
    }

    /**
     * Exclui um livro do banco de dados pelo seu ID.
     */
    @Transactional
    public void excluir(Long id) {
        // Verifica se o livro existe antes de tentar deletar
        if (!livroRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Livro com ID " + id + " não encontrado para exclusão.");
        }
        livroRepository.deleteById(id);
    }

    /**
     * Busca livros por título e os converte para DTOs.
     */
    @Transactional(readOnly = true)
    public List<LivroDTO> buscarPorTitulo(String titulo) {
        List<LivroEntity> livros = livroRepository.findByTituloContainingIgnoreCase(titulo);
        return livros.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Método auxiliar privado para converter uma Entidade em um DTO.
     * Centraliza a lógica de conversão.
     */
    private LivroDTO toDTO(LivroEntity entidade) {
        LivroDTO dto = new LivroDTO();
        BeanUtils.copyProperties(entidade, dto);
        return dto;
    }
}
