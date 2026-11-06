package com.assistaplus.assista_plus_api.avaliacao;

import com.assistaplus.assista_plus_api.serie.Serie;
import com.assistaplus.assista_plus_api.serie.SerieRepository;
import com.assistaplus.assista_plus_api.usuario.Usuario;
import com.assistaplus.assista_plus_api.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AvaliacaoService {

    @Autowired
    private AvaliacaoRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SerieRepository serieRepository;

    // Lista todas as avaliações com paginação.
    public Page<Avaliacao> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    // Busca uma avaliação pelo ID.
    public Optional<Avaliacao> buscarPorId(Long id) {
        return repository.findById(id);
    }

    // Busca avaliações pela nota.
    public List<Avaliacao> buscarPorNota(Integer nota) {
        return repository.findByNota(nota);
    }

    // Cadastra uma nova avaliação.
    public Avaliacao salvar(Avaliacao avaliacao) {

        Usuario usuario = usuarioRepository
                .findById(avaliacao.getUsuario().getId())
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado"));

        Serie serie = serieRepository
                .findById(avaliacao.getSerie().getId())
                .orElseThrow(() ->
                        new RuntimeException("Série não encontrada"));

        avaliacao.setUsuario(usuario);
        avaliacao.setSerie(serie);

        return repository.save(avaliacao);
    }

    // Atualiza uma avaliação existente.
    public Optional<Avaliacao> atualizar(Long id, Avaliacao dados) {

        return repository.findById(id)
                .map(avaliacao -> {

                    avaliacao.setNota(dados.getNota());
                    avaliacao.setComentario(dados.getComentario());

                    Usuario usuario = usuarioRepository
                            .findById(dados.getUsuario().getId())
                            .orElseThrow(() ->
                                    new RuntimeException("Usuário não encontrado"));

                    Serie serie = serieRepository
                            .findById(dados.getSerie().getId())
                            .orElseThrow(() ->
                                    new RuntimeException("Série não encontrada"));

                    avaliacao.setUsuario(usuario);
                    avaliacao.setSerie(serie);

                    return repository.save(avaliacao);
                });
    }

    // Exclui uma avaliação pelo ID.
    public boolean excluir(Long id) {

        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }

        return false;
    }
}
