package com.markDev.backend_biblioteca_springboot.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO (Data Transfer Object) padronizado para os resultados da busca de livros.
 * Não importa a fonte (Google, Open Library), os dados são sempre formatados
 * neste "envelope" antes de serem enviados para o frontend. Isto desacopla a
 * lógica da sua aplicação da estrutura de dados específica de cada API externa.
 */
@Getter
@Setter
public class BookSearchResultDTO {

	private String titulo;
	private String autor;
	private String sinopse;
	private int ano;
	private int numeroPaginas;
	private String capaUrl;
	private String idioma; // Campo para guardar o idioma, útil para a ordenação

}
