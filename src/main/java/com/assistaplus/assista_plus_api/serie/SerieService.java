package com.assistaplus.assista_plus_api.serie;

import com.assistaplus.assista_plus_api.diretor.Diretor;
import com.assistaplus.assista_plus_api.diretor.DiretorRepository;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.assistaplus.assista_plus_api.genero.Genero;
import com.assistaplus.assista_plus_api.genero.GeneroRepository;

import java.util.Optional;
import java.util.List;

//@Service informa ao Spring que esta classe faz parte
//da camada de serviço da aplicação.

//O Service concentra as regras e operações da aplicação,
//ficando entre o Controller e o Repository.
@Service
public class SerieService {

    //O Spring injeta o Repository no Service,
    //permitindo que ele faça as operações no banco.
    @Autowired
    private SerieRepository repository;

    //Repository responsável pelos dados dos diretores.

    //Precisamos dele para manter o relacionamento
    //Diretor -> Séries.
    @Autowired
    private DiretorRepository diretorRepository;

    @Autowired
    private GeneroRepository generoRepository;


    //Utilizei Page e Pageable para implementar a paginação, da listagem de séries, evitando retornar todos os registros
    //de uma única vez.
    public Page<Serie> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    // Busca séries pelo título.
    public List<Serie> buscarPorTitulo(String titulo) {
        return repository.findByTituloContainingIgnoreCase(titulo);
    }


    //Utilizei Optional porque a série procurada pode não existir.
    //Dessa forma, conseguimos tratar esse caso no Controller.
    public Optional<Serie> buscarPorId(Long id) {
        return repository.findById(id);
    }


    //O método salvar recebe a série e utiliza o Repository
    //para persistir os dados no banco.

    //Também verificamos se a série possui um diretor. Quando possui, adicionamos a série na lista de séries
    //daquele diretor.

    //Como temos um relacionamento bidirecional,
    //mantemos os dois lados da associação sincronizados.
    public Serie salvar(Serie serie) {

        // Verifica se foi informado um diretor
        if (serie.getDiretor() != null && serie.getDiretor().getId() != null) {

            // Busca o diretor existente no banco
            Diretor diretor = diretorRepository
                    .findById(serie.getDiretor().getId())
                    .orElseThrow(() ->
                            new RuntimeException("Diretor não encontrado"));

            // Define o diretor real na série.
            //
            // É isso que faz o JPA salvar o diretor_id
            // na tabela serie.
            serie.setDiretor(diretor);
        }

        // Salva a série.
        return repository.save(serie);
    }


    //No método de atualização, primeiro verifico se a série existe.
    //Depois atualizo seus dados utilizando o Repository.
    public Optional<Serie> atualizar(Long id, Serie dados) {

        return repository.findById(id)
                .map(serie -> {

                    serie.setTitulo(dados.getTitulo());
                    serie.setDescricao(dados.getDescricao());
                    serie.setAnoLancamento(dados.getAnoLancamento());
                    serie.setStatus(dados.getStatus());

                    // Permite atualizar o diretor da série.
                    serie.setDiretor(dados.getDiretor());

                    Serie serieAtualizada = repository.save(serie);

                    // Mantém o lado Diretor -> Séries sincronizado.
                    if (serieAtualizada.getDiretor() != null) {

                        Diretor diretor = diretorRepository
                                .findById(serieAtualizada.getDiretor().getId())
                                .orElse(null);

                        if (diretor != null) {
                            diretor.getSeries().add(serieAtualizada);
                            diretorRepository.save(diretor);
                        }
                    }

                    return serieAtualizada;
                });
    }


    //O método verifica se a série existe antes de excluí-la.
    //O retorno boolean indica se a exclusão foi realizada.
    public boolean excluir(Long id) {

        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }

        return false;
    }

    public Optional<Serie> adicionarGenero(Long serieId, Long generoId) {

        Optional<Serie> serieEncontrada = repository.findById(serieId);
        Optional<Genero> generoEncontrado = generoRepository.findById(generoId);

        if (serieEncontrada.isEmpty() || generoEncontrado.isEmpty()) {
            return Optional.empty();
        }

        Serie serie = serieEncontrada.get();
        Genero genero = generoEncontrado.get();

        serie.getGeneros().add(genero);

        return Optional.of(repository.save(serie));
    }

}

