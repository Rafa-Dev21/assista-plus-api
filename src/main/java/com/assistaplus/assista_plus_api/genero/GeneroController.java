package com.assistaplus.assista_plus_api.genero;

import com.assistaplus.assista_plus_api.serie.SerieController;
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
@RequestMapping("/generos")
public class GeneroController {

    @Autowired
    private GeneroService service;

    // Lista os gêneros com paginação e HATEOAS.
    @GetMapping
    public PagedModel<EntityModel<Genero>> listar(
            Pageable pageable,
            PagedResourcesAssembler<Genero> assembler) {

        Page<Genero> pagina = service.listar(pageable);

        return assembler.toModel(pagina);
    }

    // Busca gêneros pelo nome.
    @GetMapping("/buscar")
    public List<Genero> buscarPorNome(@RequestParam String nome) {
        return service.buscarPorNome(nome);
    }

    // Busca um gênero pelo ID.
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Genero>> buscarPorId(@PathVariable Long id) {

        return service.buscarPorId(id)
                .map(genero -> {

                    EntityModel<Genero> model = EntityModel.of(genero);

                    // Link para consultar o próprio gênero.
                    model.add(linkTo(methodOn(GeneroController.class)
                            .buscarPorId(id))
                            .withSelfRel());

                    // Link para atualizar o gênero.
                    model.add(linkTo(methodOn(GeneroController.class)
                            .atualizar(id, genero))
                            .withRel("atualizar"));

                    // Link para excluir o gênero.
                    model.add(linkTo(methodOn(GeneroController.class)
                            .excluir(id))
                            .withRel("excluir"));

                    // Link para consultar as séries.
                    model.add(linkTo(methodOn(SerieController.class)
                            .listar(null, null))
                            .withRel("series"));

                    return ResponseEntity.ok(model);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Cadastra um novo gênero.
    @PostMapping
    public Genero cadastrar(@Valid @RequestBody Genero genero) {
        return service.salvar(genero);
    }

    // Atualiza um gênero existente.
    @PutMapping("/{id}")
    public ResponseEntity<Genero> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Genero genero) {

        return service.atualizar(id, genero)
                .map(generoAtualizado -> ResponseEntity.ok(generoAtualizado))
                .orElse(ResponseEntity.notFound().build());
    }

    // Exclui um gênero.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        if (service.excluir(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}