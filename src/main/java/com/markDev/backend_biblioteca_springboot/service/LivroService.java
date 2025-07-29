package com.markDev.backend_biblioteca_springboot.service;

import com.markDev.backend_biblioteca_springboot.dto.LivroDTO;
import com.markDev.backend_biblioteca_springboot.entity.LivroEntity;
import com.markDev.backend_biblioteca_springboot.entity.UserEntity;
import com.markDev.backend_biblioteca_springboot.exception.RecursoNaoEncontradoException;
import com.markDev.backend_biblioteca_springboot.repository.LivroRepository;
import com.markDev.backend_biblioteca_springboot.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Camada de serviço para a lógica de negócio relacionada a livros.
 * Esta classe é o coração das operações de CRUD (Criar, Ler, Atualizar, Deletar) para livros.
 * <p>
 * A principal característica desta versão segura é que **todas as operações são
 * realizadas no contexto do utilizador atualmente autenticado**, garantindo que um
 * utilizador só possa ver e manipular os seus próprios livros.
 */
@Service
public class LivroService {

    //<editor-fold desc="Dependências">
    private final LivroRepository livroRepository;
    private final UserRepository userRepository;

    /**
     * Injeção de dependência via construtor. Esta é a prática recomendada pelo Spring
     * para garantir que as dependências são obrigatórias e que a classe é mais fácil de testar.
     *
     * @param livroRepository O repositório para acesso aos dados dos livros.
     * @param userRepository O repositório para acesso aos dados dos utilizadores.
     */
    public LivroService(LivroRepository livroRepository, UserRepository userRepository) {
        this.livroRepository = livroRepository;
        this.userRepository = userRepository;
    }
    //</editor-fold>

    //<editor-fold desc="Operações CRUD Seguras">
    /**
     * Lista todos os livros PERTENCENTES AO UTILIZADOR ATUALMENTE AUTENTICADO.
     * A segurança é garantida ao filtrar os resultados pelo utilizador obtido do contexto de segurança.
     *
     * @return Uma lista de LivroDTO contendo os livros do utilizador.
     */
    @Transactional(readOnly = true)
    public List<LivroDTO> listarTodos() {
        UserEntity currentUser = getCurrentUser();
        List<LivroEntity> livros = livroRepository.findByUser(currentUser);
        return livros.stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Insere um novo livro no banco de dados e o ASSOCIA AO UTILIZADOR ATUALMENTE AUTENTICADO.
     * Esta é a correção direta para o erro "Field 'user_id' doesn't have a default value".
     *
     * @param livroDTO Os dados do novo livro vindos do frontend.
     * @return A Entidade do livro que foi salva, contendo o novo ID gerado pelo banco de dados.
     */
    @Transactional
    public LivroEntity inserir(LivroDTO livroDTO) {
        UserEntity currentUser = getCurrentUser();
        LivroEntity livroEntity = new LivroEntity();
        // Converte o DTO recebido numa nova Entidade, ignorando o ID que vem do frontend (que pode ser temporário).
        BeanUtils.copyProperties(livroDTO, livroEntity, "id");
        
        // Passo crucial: Associa o livro ao utilizador logado ANTES de salvar.
        livroEntity.setUser(currentUser);
        
        return livroRepository.save(livroEntity);
    }

    /**
     * Altera um livro existente no banco de dados.
     * A segurança é garantida ao verificar primeiro se o livro a ser alterado
     * realmente pertence ao utilizador autenticado.
     *
     * @param id O ID do livro a ser alterado.
     * @param livroDTO Os novos dados para o livro.
     * @return A Entidade do livro que foi atualizada.
     * @throws RecursoNaoEncontradoException se o livro não for encontrado ou não pertencer ao utilizador.
     */
    @Transactional
    public LivroEntity alterar(Long id, LivroDTO livroDTO) {
        UserEntity currentUser = getCurrentUser();
        // Busca o livro apenas se o ID e o utilizador corresponderem.
        LivroEntity livroEntity = livroRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro com ID " + id + " não encontrado para este utilizador."));

        // Copia as novas propriedades do DTO para a entidade encontrada, ignorando o ID e o utilizador.
        BeanUtils.copyProperties(livroDTO, livroEntity, "id", "user");
        return livroRepository.save(livroEntity);
    }

    /**
     * Exclui um livro do banco de dados, garantindo que ele pertença ao utilizador autenticado.
     *
     * @param id O ID do livro a ser excluído.
     * @throws RecursoNaoEncontradoException se o livro não for encontrado ou não pertencer ao utilizador.
     */
    @Transactional
    public void excluir(Long id) {
        UserEntity currentUser = getCurrentUser();
        // Verifica a existência antes de deletar para fornecer uma mensagem de erro segura.
        if (!livroRepository.existsByIdAndUser(id, currentUser)) {
            throw new RecursoNaoEncontradoException("Livro com ID " + id + " não encontrado para este utilizador.");
        }
        livroRepository.deleteById(id);
    }

    /**
     * Busca livros por título, mas apenas dentro da coleção do utilizador autenticado.
     *
     * @param titulo O termo de busca.
     * @return Uma lista de LivroDTO com os resultados filtrados pelo utilizador.
     */
    @Transactional(readOnly = true)
    public List<LivroDTO> buscarPorTitulo(String titulo) {
        UserEntity currentUser = getCurrentUser();
        List<LivroEntity> livros = livroRepository.findByTituloContainingIgnoreCaseAndUser(titulo, currentUser);
        return livros.stream().map(this::toDTO).collect(Collectors.toList());
    }
    //</editor-fold>
    
    //<editor-fold desc="Métodos Auxiliares">
    /**
     * Método auxiliar privado para obter a entidade do utilizador atualmente autenticado.
     * Ele acede ao 'SecurityContextHolder' do Spring Security, que armazena as informações
     * de autenticação da requisição atual (preenchido pelo nosso JwtRequestFilter).
     *
     * @return A UserEntity correspondente ao utilizador logado.
     * @throws UsernameNotFoundException se, por algum motivo, o utilizador autenticado não for encontrado no banco de dados.
     */
    private UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Nenhum utilizador autenticado encontrado no contexto de segurança.");
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilizador '" + username + "' não encontrado no contexto de segurança."));
    }

    /**
     * Método auxiliar privado para converter uma Entidade em um DTO.
     * Centraliza a lógica de conversão, garantindo consistência.
     *
     * @param entidade O objeto LivroEntity a ser convertido.
     * @return Um objeto LivroDTO preenchido.
     */
    private LivroDTO toDTO(LivroEntity entidade) {
        LivroDTO dto = new LivroDTO();
        BeanUtils.copyProperties(entidade, dto);
        return dto;
    }
    //</editor-fold>
}
