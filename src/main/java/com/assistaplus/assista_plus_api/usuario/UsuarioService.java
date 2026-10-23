package com.assistaplus.assista_plus_api.usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    public Page<Usuario> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public List<Usuario> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome);
    }

    // --- CADASTRAR ---
    public Usuario salvar(Usuario usuario) {
        if (repository.existsByEmail(usuario.getEmail())) {
            // Lança o status 409 Conflict explicando o motivo exato
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um usuário cadastrado com este e-mail!");
        }
        return repository.save(usuario);
    }

    // --- ATUALIZAR ---
    public Optional<Usuario> atualizar(Long id, Usuario dados) {
        return repository.findById(id)
                .map(usuario -> {
                    if (!usuario.getEmail().equalsIgnoreCase(dados.getEmail()) && repository.existsByEmail(dados.getEmail())) {
                        // Lança o status 409 Conflict se tentar mudar para um e-mail que já é de outro
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Conflito: Este e-mail já está em uso por outro usuário!");
                    }

                    usuario.setNome(dados.getNome());
                    usuario.setEmail(dados.getEmail());
                    usuario.setSenha(dados.getSenha());

                    return repository.save(usuario);
                });
    }

    public boolean excluir(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}