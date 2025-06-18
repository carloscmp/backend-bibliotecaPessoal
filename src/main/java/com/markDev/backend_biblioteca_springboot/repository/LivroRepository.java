package com.markDev.backend_biblioteca_springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.markDev.backend_biblioteca_springboot.entity.LivroEntity;

public interface LivroRepository extends JpaRepository<LivroEntity, Long> {

	List<LivroEntity> findByTituloContainingIgnoreCase(String titulo);
	
}
