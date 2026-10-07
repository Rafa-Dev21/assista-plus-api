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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;

@RestController
@RequestMapping("/generos")
public class GeneroController {

    @Autowired
    private GeneroService service;

    // Lista os gêneros com paginação e HATEOAS.
    @Operation(
            summary = "Listar gêneros",
            description = "Retorna uma lista paginada de gêneros cadastrados, utilizando HATEOAS."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de gêneros retornada com sucesso."
            )
    })
    @GetMapping
    public PagedModel<EntityModel<Genero>> listar(
            Pageable pageable,
            PagedResourcesAssembler<Genero> assembler) {

        Page<Genero> pagina = service.listar(pageable);

        return assembler.toModel(pagina);
    }

    // Busca gêneros pelo nome.
    @Operation(
            summary = "Buscar gêneros por nome",
            description = "Pesquisa gêneros pelo nome informado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Busca realizada com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "O parâmetro nome não foi informado."
            )
    })
    @GetMapping("/buscar")
    public List<Genero> buscarPorNome(@RequestParam String nome) {
        return service.buscarPorNome(nome);
    }

    // Busca um gênero pelo ID.
    @Operation(
            summary = "Buscar gênero por ID",
            description = "Consulta um gênero pelo seu identificador e retorna seus dados com links HATEOAS."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Gênero encontrado com sucesso."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Gênero não encontrado."
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Genero>> buscarPorId(@Parameter(
            description = "ID do gênero que será consultado",
            example = "1",
            required = true
    )@PathVariable Long id) {

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
    @Operation(
            summary = "Cadastrar gênero",
            description = "Cadastra um novo gênero após validar os dados enviados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Gênero cadastrado com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Os dados enviados são inválidos."
            )
    })
    @PostMapping
    public Genero cadastrar(@Valid @RequestBody Genero genero) {
        return service.salvar(genero);
    }

    // Atualiza um gênero existente.
    @Operation(
            summary = "Atualizar gênero",
            description = "Atualiza os dados de um gênero existente pelo ID informado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Gênero atualizado com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Os dados enviados são inválidos."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Gênero não encontrado."
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Genero> atualizar(
            @Parameter(
                    description = "ID do gênero que será atualizado",
                    example = "1",
                    required = true
            )
            @PathVariable Long id,
            @Valid @RequestBody Genero genero) {

        return service.atualizar(id, genero)
                .map(generoAtualizado -> ResponseEntity.ok(generoAtualizado))
                .orElse(ResponseEntity.notFound().build());
    }

    // Exclui um gênero.
    @Operation(
            summary = "Excluir gênero",
            description = "Exclui um gênero cadastrado pelo seu ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Gênero excluído com sucesso."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Gênero não encontrado."
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir( @Parameter(
            description = "ID do gênero que será excluído",
            example = "1",
            required = true
    )@PathVariable Long id) {

        if (service.excluir(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}