
package com.assistaplus.assista_plus_api.ator;

import com.assistaplus.assista_plus_api.serie.Serie;
import com.assistaplus.assista_plus_api.serie.SerieRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


// @Service informa ao Spring que esta classe faz parte da camada de serviço da aplicação.

// O Service fica entre o Controller e o Repository, concentrando
// as operações e regras da aplicação.

@Service
public class AtorService {

    @Autowired
    private AtorRepository repository;

    // Repository responsável pelo acesso aos dados das séries.
    @Autowired
    private SerieRepository serieRepository;


    // Utilizamos Page e Pageable para realizar a paginação
    // da listagem de atores.
    public Page<Ator> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }


    // Optional permite tratar o caso em que o ator procurado não existe.
    public Optional<Ator> buscarPorId(Long id) {
        return repository.findById(id);
    }


    // Busca atores pelo nome informado.
    // O Repository realiza a consulta ignorando maiúsculas e minúsculas.
    public List<Ator> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome);
    }


    // Salva um novo ator no banco.
    public Ator salvar(Ator ator) {
        return repository.save(ator);
    }


    // Primeiro verifica se o ator existe.
    // Depois atualiza seus dados e salva no banco.
    public Optional<Ator> atualizar(Long id, Ator dados) {

        return repository.findById(id)
                .map(ator -> {
                    ator.setNome(dados.getNome());
                    ator.setNacionalidade(dados.getNacionalidade());

                    return repository.save(ator);
                });
    }


    // Verifica se o ator existe antes de excluir.
    // Retorna true se a exclusão foi realizada.
    public boolean excluir(Long id) {

        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }

        return false;
    }


    // Adiciona uma série ao ator.
    // Recebemos o ID do ator e o ID da série.
    public Optional<Ator> adicionarSerie(Long atorId, Long serieId) {

        // Busca o ator pelo ID.
        Optional<Ator> atorEncontrado = repository.findById(atorId);

        // Busca a série pelo ID.
        Optional<Serie> serieEncontrada = serieRepository.findById(serieId);

        // Se o ator ou a série não existirem, retorna vazio.
        if (atorEncontrado.isEmpty() || serieEncontrada.isEmpty()) {
            return Optional.empty();
        }

        // Pega o ator encontrado.
        Ator ator = atorEncontrado.get();

        // Pega a série encontrada.
        Serie serie = serieEncontrada.get();

        // Adiciona a série ao conjunto de séries do ator.
        ator.getSeries().add(serie);

        // Salva o relacionamento no banco.
        return Optional.of(repository.save(ator));
    }

}
