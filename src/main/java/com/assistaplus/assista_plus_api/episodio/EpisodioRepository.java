package com.assistaplus.assista_plus_api.episodio;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// O Repository é responsável pelo acesso aos dados da entidade Episodio.
// O JpaRepository já disponibiliza as operações básicas de CRUD e paginação.

public interface EpisodioRepository extends JpaRepository<Episodio, Long> {

    // Busca episódios pelo título, ignorando maiúsculas e minúsculas.
    List<Episodio> findByTituloContainingIgnoreCase(String titulo);
}