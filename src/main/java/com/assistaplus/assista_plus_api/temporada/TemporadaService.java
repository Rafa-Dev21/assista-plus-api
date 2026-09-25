package com.assistaplus.assista_plus_api.temporada;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

// @Service informa ao Spring que esta classe faz parte
// da camada de serviço da aplicação.

// O Service fica entre o Controller e o Repository,
// concentrando as operações e regras da aplicação.

@Service
public class TemporadaService {

    @Autowired
    private TemporadaRepository repository;

    // Utilizamos Page e Pageable para realizar a paginação
    // da listagem de temporadas.
    public Page<Temporada> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    // Optional permite tratar o caso em que a temporada
    // procurada não existe.
    public Optional<Temporada> buscarPorId(Long id) {
        return repository.findById(id);
    }

    // Salva uma nova temporada no banco.
    public Temporada salvar(Temporada temporada) {
        return repository.save(temporada);
    }

    // Primeiro verifica se a temporada existe.
    // Depois atualiza seus dados e salva no banco.
    public Optional<Temporada> atualizar(Long id, Temporada dados) {

        return repository.findById(id)
                .map(temporada -> {
                    temporada.setNumero(dados.getNumero());
                    temporada.setSerie(dados.getSerie());

                    return repository.save(temporada);
                });
    }

    // Verifica se a temporada existe antes de excluir.
    // Retorna true se a exclusão foi realizada.
    public boolean excluir(Long id) {

        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }

        return false;
    }
}