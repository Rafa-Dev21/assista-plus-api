package com.assistaplus.assista_plus_api.idempotencia;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotenciaRepository extends JpaRepository<IdempotenciaRegistro, String> {
}