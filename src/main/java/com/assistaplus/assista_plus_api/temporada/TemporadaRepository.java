package com.assistaplus.assista_plus_api.temporada;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// O Repository é responsável pelo acesso aos dados da entidade Temporada.
// O JpaRepository já fornece as operações básicas de CRUD e paginação.

public interface TemporadaRepository extends JpaRepository<Temporada, Long> {

    // Validações de unicidade (opcional caso queira impedir número de temporada duplicado na mesma série)
    boolean existsByNumero(Integer numero);
    boolean existsByNumeroAndIdNot(Integer numero, Long id);

    // Busca temporadas pelo número.
    List<Temporada> findByNumero(Integer numero);
}