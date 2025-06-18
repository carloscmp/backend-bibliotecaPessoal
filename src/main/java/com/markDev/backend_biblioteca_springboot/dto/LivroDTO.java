package com.markDev.backend_biblioteca_springboot.dto;

import org.springframework.beans.BeanUtils;

import com.markDev.backend_biblioteca_springboot.entity.LivroEntity;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LivroDTO {

	private Long id;
	private String titulo;
	private String autor;
	private Integer ano;
	
	@Size(max = 2000)
	private String sinopse;
	private Integer numeroPaginas;
	private byte[] capa;
	private byte[] contraCapa;
	
	public LivroDTO (LivroEntity livro) {
		
		BeanUtils.copyProperties(livro, this);
		
	}
	
}
