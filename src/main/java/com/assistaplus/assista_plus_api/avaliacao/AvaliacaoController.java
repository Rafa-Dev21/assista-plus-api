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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springdoc.core.annotations.ParameterObject;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;

@RestController
@RequestMapping("/avaliacoes")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoService service;

    // Lista todas as avaliações com paginação.

    @Operation(
            summary = "Listar avaliações",
            description = "Retorna uma lista paginada de avaliações cadastradas, utilizando HATEOAS."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de avaliações retornada com sucesso."
            )
    })
    @GetMapping
    public PagedModel<EntityModel<Avaliacao>> listar(
            @ParameterObject Pageable pageable,
            @Parameter(hidden = true) PagedResourcesAssembler<Avaliacao> assembler) {

        Page<Avaliacao> pagina = service.listar(pageable);

        return assembler.toModel(pagina);
    }

    // Busca uma avaliação pelo ID.
    @Operation(
            summary = "Buscar avaliação por ID",
            description = "Consulta uma avaliação pelo seu identificador e retorna seus dados com links HATEOAS para o usuário e a série relacionados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Avaliação encontrada com sucesso."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Avaliação não encontrada."
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Avaliacao>> buscarPorId(  @Parameter(
            description = "ID da avaliação que será consultada",
            example = "1",
            required = true
    )@PathVariable Long id) {

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
    @Operation(
            summary = "Buscar avaliações por nota",
            description = "Pesquisa avaliações pela nota informada."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Busca realizada com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "O parâmetro nota não foi informado ou possui valor inválido."
            )
    })
    @GetMapping("/buscar")
    public List<Avaliacao> buscarPorNota(@Parameter(
            description = "Nota utilizada para pesquisar avaliações",
            example = "5",
            required = true
    )@RequestParam Integer nota) {
        return service.buscarPorNota(nota);
    }

    // Cadastra uma nova avaliação.
    @Operation(
            summary = "Cadastrar avaliação",
            description = "Cadastra uma nova avaliação após validar os dados enviados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Avaliação cadastrada com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Os dados enviados são inválidos."
            )
    })
    @PostMapping
    public ResponseEntity<Avaliacao> cadastrar(
            @Valid @RequestBody Avaliacao avaliacao) {

        return ResponseEntity
                .status(201)
                .body(service.salvar(avaliacao));
    }

    // Atualiza uma avaliação existente.
    @Operation(
            summary = "Atualizar avaliação",
            description = "Atualiza os dados de uma avaliação existente pelo ID informado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Avaliação atualizada com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Os dados enviados são inválidos."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Avaliação não encontrada."
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Avaliacao> atualizar(
            @Parameter(
                    description = "ID da avaliação que será atualizada",
                    example = "1",
                    required = true
            )
            @PathVariable Long id,
            @Valid @RequestBody Avaliacao avaliacao) {

        return service.atualizar(id, avaliacao)
                .map(avaliacaoAtualizada -> ResponseEntity.ok(avaliacaoAtualizada))
                .orElse(ResponseEntity.notFound().build());
    }

    // Exclui uma avaliação pelo ID.
    @Operation(
            summary = "Excluir avaliação",
            description = "Exclui uma avaliação cadastrada pelo seu ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Avaliação excluída com sucesso."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Avaliação não encontrada."
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir( @Parameter(
            description = "ID da avaliação que será excluída",
            example = "1",
            required = true
    )@PathVariable Long id) {

        if (service.excluir(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}