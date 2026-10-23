package com.assistaplus.assista_plus_api.diretor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    // Pageable recebe informações como:
    // page = número da página
    // size = quantidade de registros por página
    public Page<Diretor> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }


    //Busca um diretor pelo ID.

    //Optional evita retornar null diretamente.
    public Optional<Diretor> buscarPorId(Long id) { return repository.findByIdComSeries(id); }


    //Busca diretores pelo nome.

    //Esse método será utilizado pelo endpoint:
    //GET /diretores/buscar?nome=...
    public List<Diretor> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome);
    }


    //Cadastra um novo diretor.
    public Diretor salvar(Diretor diretor) {
        return repository.save(diretor);
    }


    //Atualiza um diretor existente.

    //Primeiro procura pelo ID.
    //Se encontrar, atualiza os dados e salva.
    public Optional<Diretor> atualizar(Long id, Diretor dados) {

        return repository.findById(id)
                .map(diretor -> {

                    diretor.setNome(dados.getNome());
                    diretor.setNacionalidade(dados.getNacionalidade());

                    return repository.save(diretor);
                });
    }


    //Exclui um diretor pelo ID.

    //Retorna true quando o diretor existe e foi excluído.
    //Retorna false quando o ID não existe.
    public boolean excluir(Long id) {

        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }

        return false;
    }
}

