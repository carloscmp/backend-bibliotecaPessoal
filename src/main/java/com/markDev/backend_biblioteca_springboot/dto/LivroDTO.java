package com.markDev.backend_biblioteca_springboot.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.markDev.backend_biblioteca_springboot.entity.LivroEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@Schema(description = "Representa os dados de um livro para transferência entre o cliente e a API")
@Getter
@Setter
@NoArgsConstructor // Mantém o construtor padrão, que é necessário para o Jackson
public class LivroDTO {

	@Schema(description = "Identificador único do livro", example = "1")
	private Long id;

	@Schema(description = "Título do livro", example = "O Senhor dos Anéis: Volume único")
	@NotBlank(message = "O título não pode ser vazio ou nulo.")
	@Size(max = 255, message = "O título não pode exceder 255 caracteres.")
	private String titulo;

	@Schema(description = "Autor do livro", example = "J.R.R. Tolkien")
	@NotBlank(message = "O autor não pode ser vazio ou nulo.")
	@Size(max = 255, message = "O autor não pode exceder 255 caracteres.")
	private String autor;

	@Schema(description = "Ano de publicação", example = "2017")
	@Min(value = 0, message = "O ano não pode ser um número negativo.")
	private Integer ano;

	@Schema(description = "Sinopse do livro", example = "Este volume está composto pela primeira (A Sociedade do Anel), segunda (As Duas Torres) e terceira parte (O Retorno do Rei) da grande obra de ficção fantástica de J. R. R. Tolkien, O Senhor dos Anéis.")
	@Size(max = 2000, message = "A sinopse não pode exceder 2000 caracteres.")
	private String sinopse;

	@Schema(description = "Numero de paginas", example = "1232")
	@Min(value = 0, message = "O número de páginas não pode ser negativo.")
	private Integer numeroPaginas;

	private boolean lido;

	private boolean emprestado;

	private String emprestadoPara;

	private byte[] capa;

	@JsonIgnore
	private byte[] contraCapa;

	/**
	 * Construtor para converter facilmente uma Entidade em um DTO. É chamado no
	 * Controller para preparar a resposta para o frontend.
	 *
	 * @param entidade O objeto LivroEntity vindo do banco de dados.
	 */
	public LivroDTO(LivroEntity entidade) {
		// Copia todas as propriedades com nomes correspondentes da entidade para este
		// DTO.
		BeanUtils.copyProperties(entidade, this);
	}
}
