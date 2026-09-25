package com.assistaplus.assista_plus_api.episodio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

//@Service informa ao Spring que esta classe faz parte
//da camada de serviço da aplicação.

//O Service fica entre o Controller e o Repository,
//concentrando as operações e regras da aplicação.

@Service
public class EpisodioService {

    @Autowired
    private EpisodioRepository repository;

    //Utilizamos Page e Pageable para realizar a paginação
    //da listagem de episódios.
    public Page<Episodio> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    //Optional permite tratar o caso em que o episódio
    //procurado não existe.
    public Optional<Episodio> buscarPorId(Long id) {
        return repository.findById(id);
    }

    //Salva um novo episódio no banco.
    public Episodio salvar(Episodio episodio) {
        return repository.save(episodio);
    }

    //Primeiro verifica se o episódio existe.
    //Depois atualiza seus dados e salva no banco.
    public Optional<Episodio> atualizar(Long id, Episodio dados) {

        return repository.findById(id)
                .map(episodio -> {
                    episodio.setNumero(dados.getNumero());
                    episodio.setTitulo(dados.getTitulo());
                    episodio.setDuracao(dados.getDuracao());
                    episodio.setTemporada(dados.getTemporada());

                    return repository.save(episodio);
                });
    }

    //Verifica se o episódio existe antes de excluir.
    //Retorna true se a exclusão foi realizada.
    public boolean excluir(Long id) {

        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }

        return false;
    }

}