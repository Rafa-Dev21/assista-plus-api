package com.assistaplus.assista_plus_api.genero;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneroRepository extends JpaRepository<Genero, Long> {

    boolean existsByNome(String nome);

    boolean existsByNomeAndIdNot(String nome, Long id);

    List<Genero> findByNomeContainingIgnoreCase(String nome);

}