package com.markDev.backend_biblioteca_springboot.dto;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.markDev.backend_biblioteca_springboot.entity.LivroEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Representa os dados de um livro para transferência entre o cliente e a API")
@Getter
@Setter
@NoArgsConstructor
public class LivroDTO {

	@Schema(description = "Identificador único do livro", example = "1")
	private Long id;
	@Schema(description = "Título do livro", example = "O Senhor dos Anéis: Volume único")
	private String titulo;
	@Schema(description = "Autor do livro", example = "J.R.R. Tolkien")
	private String autor;
	@Schema(description = "Ano de publicação", example = "2017")
	private Integer ano;
	@Schema(description = "Sinopse do livro", example = "Este volume está composto pela primeira (A Sociedade do Anel), segunda (As Duas Torres) e terceira parte (O Retorno do Rei) da grande obra de ficção fantástica de J. R. R. Tolkien, O Senhor dos Anéis.")
	@Size(max = 2000)
	private String sinopse;
	@Schema(description = "Numero de paginas", example = "1232")
	private Integer numeroPaginas;
	
	private byte[] capa;
	@JsonIgnore 
	private byte[] contraCapa;
	
//	public LivroDTO (LivroEntity livro) {
//		
//		BeanUtils.copyProperties(livro, this);
//		
//	}
	
}
