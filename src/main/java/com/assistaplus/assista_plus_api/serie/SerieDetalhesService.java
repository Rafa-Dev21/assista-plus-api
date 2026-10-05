package com.assistaplus.assista_plus_api.serie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SerieDetalhesService {

    @Autowired
    private SerieDetalhesRepository repository;

    @Autowired
    private SerieRepository serieRepository;

    // Lista os detalhes das séries com paginação.
    public Page<SerieDetalhes> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    // Busca os detalhes pelo país de origem.
    public List<SerieDetalhes> buscarPorPais(String paisOrigem) {
        return repository.findByPaisOrigemContainingIgnoreCase(paisOrigem);
    }

    // Cadastra os detalhes de uma série.
    public SerieDetalhes salvar(SerieDetalhes detalhes) {

        if (detalhes.getSerie() != null && detalhes.getSerie().getId() != null) {

            Serie serie = serieRepository
                    .findById(detalhes.getSerie().getId())
                    .orElseThrow(() ->
                            new RuntimeException("Série não encontrada"));

            detalhes.setSerie(serie);
        }

        return repository.save(detalhes);
    }

    // Busca os detalhes pelo ID.
    public Optional<SerieDetalhes> buscarPorId(Long id) {
        return repository.findById(id);
    }

    // Atualiza os detalhes de uma série existente.
    public Optional<SerieDetalhes> atualizar(
            Long id,
            SerieDetalhes dados) {

        return repository.findById(id)
                .map(detalhes -> {

                    detalhes.setIdiomaOriginal(dados.getIdiomaOriginal());
                    detalhes.setPaisOrigem(dados.getPaisOrigem());
                    detalhes.setClassificacaoIndicativa(
                            dados.getClassificacaoIndicativa()
                    );

                    return repository.save(detalhes);
                });
    }

    // Exclui os detalhes pelo ID.
    public boolean excluir(Long id) {

        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }

        return false;
    }
}