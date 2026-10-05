package com.assistaplus.assista_plus_api.usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    // Lista os usuários com paginação.
    public Page<Usuario> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    // Busca um usuário pelo ID.
    public Optional<Usuario> buscarPorId(Long id) {
        return repository.findById(id);
    }

    // Busca usuários pelo nome.
    public List<Usuario> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome);
    }

    // Cadastra um novo usuário.
    public Usuario salvar(Usuario usuario) {
        return repository.save(usuario);
    }

    // Atualiza um usuário existente.
    public Optional<Usuario> atualizar(Long id, Usuario dados) {
        return repository.findById(id)
                .map(usuario -> {
                    usuario.setNome(dados.getNome());
                    usuario.setEmail(dados.getEmail());

                    return repository.save(usuario);
                });
    }

    // Exclui um usuário pelo ID.
    public boolean excluir(Long id) {

        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }

        return false;
    }
}