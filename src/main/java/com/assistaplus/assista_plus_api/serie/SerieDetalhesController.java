package com.assistaplus.assista_plus_api.serie;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;

@RestController
@RequestMapping("/serie-detalhes")
public class SerieDetalhesController {

    @Autowired
    private SerieDetalhesService service;

    // Lista os detalhes das séries com paginação.
    @GetMapping
    public PagedModel<EntityModel<SerieDetalhes>> listar(
            Pageable pageable,
            PagedResourcesAssembler<SerieDetalhes> assembler) {

        Page<SerieDetalhes> pagina = service.listar(pageable);

        return assembler.toModel(pagina);
    }

    // Busca detalhes pelo país de origem.
    @GetMapping("/buscar")
    public List<SerieDetalhes> buscarPorPais(
            @RequestParam String paisOrigem) {

        return service.buscarPorPais(paisOrigem);
    }

    // Busca detalhes pelo ID.
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<SerieDetalhes>> buscarPorId(
            @PathVariable Long id) {

        return service.buscarPorId(id)
                .map(detalhes -> {

                    EntityModel<SerieDetalhes> model = EntityModel.of(detalhes);

                    // Link para consultar os próprios detalhes.
                    model.add(linkTo(methodOn(SerieDetalhesController.class)
                            .buscarPorId(id))
                            .withSelfRel());

                    // Link para atualizar os detalhes.
                    model.add(linkTo(methodOn(SerieDetalhesController.class)
                            .atualizar(id, detalhes))
                            .withRel("atualizar"));

                    // Link para excluir os detalhes.
                    model.add(linkTo(methodOn(SerieDetalhesController.class)
                            .excluir(id))
                            .withRel("excluir"));

                    // Link para consultar a série relacionada.
                    if (detalhes.getSerie() != null
                            && detalhes.getSerie().getId() != null) {

                        model.add(linkTo(methodOn(SerieController.class)
                                .buscarPorId(detalhes.getSerie().getId()))
                                .withRel("serie"));
                    }

                    return ResponseEntity.ok(model);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Cadastra os detalhes de uma série.
    @PostMapping
    public ResponseEntity<SerieDetalhes> cadastrar(
            @Valid @RequestBody SerieDetalhes detalhes) {

        return ResponseEntity
                .status(201)
                .body(service.salvar(detalhes));
    }

    // Atualiza os detalhes de uma série.
    @PutMapping("/{id}")
    public ResponseEntity<SerieDetalhes> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody SerieDetalhes detalhes) {

        return service.atualizar(id, detalhes)
                .map(detalhesAtualizados -> ResponseEntity.ok(detalhesAtualizados))
                .orElse(ResponseEntity.notFound().build());
    }

    // Exclui os detalhes pelo ID.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id) {

        if (service.excluir(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}