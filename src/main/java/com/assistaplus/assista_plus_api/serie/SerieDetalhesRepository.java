package com.assistaplus.assista_plus_api.serie;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SerieDetalhesRepository extends JpaRepository<SerieDetalhes, Long> {

    // Busca detalhes pelo país de origem, ignorando maiúsculas e minúsculas.
    List<SerieDetalhes> findByPaisOrigemContainingIgnoreCase(String paisOrigem);
}