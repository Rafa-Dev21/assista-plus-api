package com.assistaplus.assista_plus_api.serie;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SerieRepository extends JpaRepository<Serie, Long> {

    // Validações de unicidade para evitar títulos duplicados
    boolean existsByTitulo(String titulo);
    boolean existsByTituloAndIdNot(String titulo, Long id);

    // Busca séries pelo título, ignorando maiúsculas e minúsculas.
    List<Serie> findByTituloContainingIgnoreCase(String titulo);
}