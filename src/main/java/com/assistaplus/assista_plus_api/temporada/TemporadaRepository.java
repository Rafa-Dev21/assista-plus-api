package com.assistaplus.assista_plus_api.temporada;

import org.springframework.data.jpa.repository.JpaRepository;

// O Repository é responsável pelo acesso aos dados da entidade Temporada.

// Ao estender JpaRepository, o Spring Data JPA já fornece
// operações prontas de CRUD.

// O Repository é a camada responsável pela comunicação com o banco.

// O JpaRepository já disponibiliza as operações básicas de CRUD.

public interface TemporadaRepository extends JpaRepository<Temporada, Long> {

}