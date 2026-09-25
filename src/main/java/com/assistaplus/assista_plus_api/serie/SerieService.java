package com.assistaplus.assista_plus_api.serie;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

// @Service informa ao Spring que esta classe faz parte
// da camada de serviço da aplicação.

//O Service concentra as regras e operações da aplicação,
//ficando entre o Controller e o Repository.


@Service
public class SerieService {

    //O Spring injeta o Repository no Service,permitindo que ele faça as operações no banco

    @Autowired
    private SerieRepository repository;


    //Utilizei Page e Pageable para implementar a paginação
    //da listagem de séries, evitando retornar todos os registros
    //de uma única vez.
    public Page<Serie> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    // Utilizei Optional porque a série procurada pode não existir
    // Dessa forma, conseguimos tratar esse caso no Controller
    public Optional<Serie> buscarPorId(Long id) {
        return repository.findById(id);
    }

    //O método salvar recebe a série e utiliza o Repository
    //para persistir os dados no banco.
    public Serie salvar(Serie serie) {
        return repository.save(serie);
    }

    //No método de atualização, primeiro verifico se a série existe. Depois atualizo seus dados utilizando o
    // Repository
    public Optional<Serie> atualizar(Long id, Serie dados) {

        return repository.findById(id)
                .map(serie -> {
                    serie.setTitulo(dados.getTitulo());
                    serie.setDescricao(dados.getDescricao());
                    serie.setAnoLancamento(dados.getAnoLancamento());
                    serie.setStatus(dados.getStatus());

                    return repository.save(serie);
                });
    }

    //O método verifica se a série existe antes de excluí-la. O retorno boolean indica se a exclusão foi realizada
    public boolean excluir(Long id) {

        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }

        return false;
    }



}