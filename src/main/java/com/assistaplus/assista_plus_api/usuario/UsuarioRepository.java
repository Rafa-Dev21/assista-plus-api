package com.assistaplus.assista_plus_api.usuario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Método necessário para verificar se já existe um usuário com o e-mail informado
    boolean existsByEmail(String email);

    List<Usuario> findByNomeContainingIgnoreCase(String nome);
}