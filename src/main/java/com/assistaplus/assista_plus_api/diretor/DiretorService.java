package com.assistaplus.assista_plus_api.diretor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

//@Service informa ao Spring que esta classe contém
//a lógica de negócio relacionada aos diretores.
@Service
public class DiretorService {

    // Repository responsável pelo acesso aos dados dos diretores.
    @Autowired
    private DiretorRepository repository;


    // Lista todos os diretores com paginação.
    public Page<Diretor> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }


    //Busca um diretor pelo ID.
    public Optional<Diretor> buscarPorId(Long id) { return repository.findByIdComSeries(id); }


    //Busca diretores pelo nome.
    public List<Diretor> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome);
    }


    //Cadastra um novo diretor.
    public Diretor salvar(Diretor diretor) {
        if (repository.existsByNome(diretor.getNome())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um diretor cadastrado com o nome '" + diretor.getNome() + "'.");
        }
        return repository.save(diretor);
    }


    //Atualiza um diretor existente.
    public Optional<Diretor> atualizar(Long id, Diretor dados) {

        return repository.findById(id)
                .map(diretor -> {
                    if (repository.existsByNomeAndIdNot(dados.getNome(), id)) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe outro diretor cadastrado com o nome '" + dados.getNome() + "'.");
                    }

                    diretor.setNome(dados.getNome());
                    diretor.setNacionalidade(dados.getNacionalidade());

                    return repository.save(diretor);
                });
    }


    //Exclui um diretor pelo ID.
    public boolean excluir(Long id) {

        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }

        return false;
    }
}