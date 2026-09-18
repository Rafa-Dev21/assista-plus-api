package com.assistaplus.assista_plus_api.serie;

import org.springframework.data.jpa.repository.JpaRepository;

// O Repository é responsável pelo acesso aos dados da entidade Serie.

// Ao estender JpaRepository, o Spring Data JPA já fornece
// operações prontas como salvar, buscar, atualizar e excluir.


// "O Repository é a camada responsável pela comunicação com o banco.
// O JpaRepository já disponibiliza as operações básicas de CRUD."


public interface SerieRepository extends JpaRepository<Serie, Long> {



}