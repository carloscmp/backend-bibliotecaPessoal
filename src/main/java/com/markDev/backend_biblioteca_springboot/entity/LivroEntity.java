package com.markDev.backend_biblioteca_springboot.entity;

import org.springframework.beans.BeanUtils;

import com.markDev.backend_biblioteca_springboot.dto.LivroDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "MK_LIVRO")
public class LivroEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private String titulo;
	@Column(nullable = false)
	private String autor;
	@Column(nullable = true)
	private Integer ano;
	@Column(length = 2000, nullable = true)
	private String sinopse;
	@Column(nullable = true)
	private Integer numeroPaginas;
	@Lob
	private byte[] capa;
	@Lob
	private byte[] contraCapa;
	
public LivroEntity (LivroDTO livro) {
		
		BeanUtils.copyProperties(livro, this);
		
	}
}
