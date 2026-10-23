package com.assistaplus.assista_plus_api.temporada;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// O Repository é responsável pelo acesso aos dados da entidade Temporada.
// O JpaRepository já fornece as operações básicas de CRUD e paginação.

public interface TemporadaRepository extends JpaRepository<Temporada, Long> {

    // Busca temporadas pelo número.
    List<Temporada> findByNumero(Integer numero);
}