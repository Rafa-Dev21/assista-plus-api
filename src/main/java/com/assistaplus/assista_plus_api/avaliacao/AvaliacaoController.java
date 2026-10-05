package com.assistaplus.assista_plus_api.avaliacao;

import com.assistaplus.assista_plus_api.serie.SerieController;
import com.assistaplus.assista_plus_api.usuario.UsuarioController;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;

@RestController
@RequestMapping("/avaliacoes")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoService service;

    // Lista todas as avaliações com paginação.
    @GetMapping
    public PagedModel<EntityModel<Avaliacao>> listar(
            Pageable pageable,
            PagedResourcesAssembler<Avaliacao> assembler) {

        Page<Avaliacao> pagina = service.listar(pageable);

        return assembler.toModel(pagina);
    }

    // Busca uma avaliação pelo ID.
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Avaliacao>> buscarPorId(@PathVariable Long id) {

        return service.buscarPorId(id)
                .map(avaliacao -> {

                    EntityModel<Avaliacao> model = EntityModel.of(avaliacao);

                    // Link para consultar a própria avaliação.
                    model.add(linkTo(methodOn(AvaliacaoController.class)
                            .buscarPorId(id))
                            .withSelfRel());

                    // Link para atualizar a avaliação.
                    model.add(linkTo(methodOn(AvaliacaoController.class)
                            .atualizar(id, avaliacao))
                            .withRel("atualizar"));

                    // Link para excluir a avaliação.
                    model.add(linkTo(methodOn(AvaliacaoController.class)
                            .excluir(id))
                            .withRel("excluir"));

                    // Link para consultar o usuário da avaliação.
                    if (avaliacao.getUsuario() != null
                            && avaliacao.getUsuario().getId() != null) {

                        model.add(linkTo(methodOn(UsuarioController.class)
                                .buscarPorId(avaliacao.getUsuario().getId()))
                                .withRel("usuario"));
                    }

                    // Link para consultar a série da avaliação.
                    if (avaliacao.getSerie() != null
                            && avaliacao.getSerie().getId() != null) {

                        model.add(linkTo(methodOn(SerieController.class)
                                .buscarPorId(avaliacao.getSerie().getId()))
                                .withRel("serie"));
                    }

                    return ResponseEntity.ok(model);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Busca avaliações pela nota.
    @GetMapping("/buscar")
    public List<Avaliacao> buscarPorNota(@RequestParam Integer nota) {
        return service.buscarPorNota(nota);
    }

    // Cadastra uma nova avaliação.
    @PostMapping
    public ResponseEntity<Avaliacao> cadastrar(
            @Valid @RequestBody Avaliacao avaliacao) {

        return ResponseEntity
                .status(201)
                .body(service.salvar(avaliacao));
    }

    // Atualiza uma avaliação existente.
    @PutMapping("/{id}")
    public ResponseEntity<Avaliacao> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Avaliacao avaliacao) {

        return service.atualizar(id, avaliacao)
                .map(avaliacaoAtualizada -> ResponseEntity.ok(avaliacaoAtualizada))
                .orElse(ResponseEntity.notFound().build());
    }

    // Exclui uma avaliação pelo ID.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        if (service.excluir(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}