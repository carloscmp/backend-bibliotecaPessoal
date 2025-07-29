package com.markDev.backend_biblioteca_springboot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor // O construtor sem argumentos do Lombok é suficiente
@Table(name = "MK_LIVRO")
public class LivroEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	@JsonIgnore
	private UserEntity user;

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

	@Column(nullable = false)
	private boolean lido = false; // Boa prática: inicializar com um valor padrão

	@Column(nullable = false)
	private boolean emprestado = false; // Boa prática: inicializar

	@Column(nullable = true)
	private String emprestadoPara;

	@Column(nullable = true)
	private Integer numeroPaginas;

	@Lob
	@Column(columnDefinition = "LONGBLOB") // Boa prática para blobs
	private byte[] capa;

	@Lob
	@Column(columnDefinition = "LONGBLOB") // Boa prática para blobs
	private byte[] contraCapa;

}