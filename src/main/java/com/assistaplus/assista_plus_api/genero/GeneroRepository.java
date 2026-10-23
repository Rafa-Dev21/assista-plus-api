package com.assistaplus.assista_plus_api.genero;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GeneroRepository extends JpaRepository<Genero, Long> {

    List<Genero> findByNomeContainingIgnoreCase(String nome);
}