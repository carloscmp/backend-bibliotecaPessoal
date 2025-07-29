package com.markDev.backend_biblioteca_springboot.repository;

import com.markDev.backend_biblioteca_springboot.entity.LivroEntity;
import com.markDev.backend_biblioteca_springboot.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LivroRepository extends JpaRepository<LivroEntity, Long> {

    /**
     * Encontra todos os livros associados a um utilizador específico.
     * Usado para listar a biblioteca do utilizador logado.
     */
    List<LivroEntity> findByUser(UserEntity user);

    /**
     * Encontra um livro pelo seu ID, mas apenas se ele pertencer ao utilizador especificado.
     * Crucial para garantir que um utilizador não possa editar ou ver um livro de outro.
     */
    Optional<LivroEntity> findByIdAndUser(Long id, UserEntity user);

    /**
     * Verifica de forma eficiente se um livro com um certo ID pertence a um utilizador.
     * Usado na lógica de exclusão.
     */
    boolean existsByIdAndUser(Long id, UserEntity user);

    /**
     * Busca livros por título, mas apenas dentro da coleção do utilizador especificado.
     */
    List<LivroEntity> findByTituloContainingIgnoreCaseAndUser(String titulo, UserEntity user);
}
