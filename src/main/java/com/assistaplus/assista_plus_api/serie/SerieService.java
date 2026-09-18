package com.assistaplus.assista_plus_api.serie;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    //O método salvar recebe a série e utiliza o Repository
    //para persistir os dados no banco.
    public Serie salvar(Serie serie) {
        return repository.save(serie);
    }

}