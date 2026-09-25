package com.assistaplus.assista_plus_api.episodio;

import org.springframework.data.jpa.repository.JpaRepository;

// O Repository é responsável pelo acesso aos dados da entidade Episodio.

//O Repository é a camada responsável pela comunicação com o banco. O JpaRepository já disponibiliza as
// operações básicas de CRUD.


public interface EpisodioRepository extends JpaRepository<Episodio, Long> {
}