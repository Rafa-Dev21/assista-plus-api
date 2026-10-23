package com.assistaplus.assista_plus_api.genero;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Service
public class GeneroService {

    @Autowired
    private GeneroRepository repository;

    public Page<Genero> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Optional<Genero> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Genero salvar(Genero genero) {
        return repository.save(genero);
    }

    public Optional<Genero> atualizar(Long id, Genero dados) {
        return repository.findById(id)
                .map(genero -> {

                    genero.setNome(dados.getNome());

                    return repository.save(genero);
                });
    }

    public boolean excluir(Long id) {

        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }

        return false;
    }

    public List<Genero> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome);
    }
}