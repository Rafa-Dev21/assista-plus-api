package com.assistaplus.assista_plus_api.ator;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


// O Repository é responsável pelo acesso aos dados da entidade Ator.

// Ao estender JpaRepository, o Spring Data JPA já fornece
// operações prontas de CRUD.

// "O Repository é a camada responsável pela comunicação com o banco.
// O JpaRepository já disponibiliza as operações básicas de CRUD."

public interface AtorRepository extends JpaRepository<Ator, Long> {

    // Busca atores cujo nome contenha o texto informado. IgnoreCase faz a busca ignorando maiúsculas e minúsculas.
    List<Ator> findByNomeContainingIgnoreCase(String nome);

}