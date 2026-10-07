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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;

@RestController
@RequestMapping("/serie-detalhes")
public class SerieDetalhesController {

    @Autowired
    private SerieDetalhesService service;

    // Lista os detalhes das séries com paginação.
    @Operation(
            summary = "Listar detalhes das séries",
            description = "Retorna uma lista paginada dos detalhes das séries cadastrados, utilizando HATEOAS."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de detalhes retornada com sucesso."
            )
    })
    @GetMapping
    public PagedModel<EntityModel<SerieDetalhes>> listar(
            Pageable pageable,
            PagedResourcesAssembler<SerieDetalhes> assembler) {

        Page<SerieDetalhes> pagina = service.listar(pageable);

        return assembler.toModel(pagina);
    }

    // Busca detalhes pelo país de origem.
    @Operation(
            summary = "Buscar detalhes por país de origem",
            description = "Pesquisa os detalhes das séries pelo país de origem informado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Busca realizada com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "O parâmetro país de origem não foi informado."
            )
    })
    @GetMapping("/buscar")
    public List<SerieDetalhes> buscarPorPais(
            @Parameter(
                    description = "País de origem utilizado na pesquisa",
                    example = "Estados Unidos",
                    required = true
            )
            @RequestParam String paisOrigem) {

        return service.buscarPorPais(paisOrigem);
    }

    // Busca detalhes pelo ID.
    @Operation(
            summary = "Buscar detalhes por ID",
            description = "Consulta os detalhes de uma série pelo identificador e retorna os dados com links HATEOAS."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Detalhes encontrados com sucesso."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Detalhes não encontrados."
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<SerieDetalhes>> buscarPorId(
            @Parameter(
                    description = "ID dos detalhes que serão consultados",
                    example = "1",
                    required = true
            )
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
    @Operation(
            summary = "Cadastrar detalhes de série",
            description = "Cadastra os detalhes de uma série após validar os dados enviados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Detalhes cadastrados com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Os dados enviados são inválidos."
            )
    })
    @PostMapping
    public ResponseEntity<SerieDetalhes> cadastrar(
            @Valid @RequestBody SerieDetalhes detalhes) {

        return ResponseEntity
                .status(201)
                .body(service.salvar(detalhes));
    }

    // Atualiza os detalhes de uma série.
    @Operation(
            summary = "Atualizar detalhes de série",
            description = "Atualiza os detalhes de uma série existente pelo ID informado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Detalhes atualizados com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Os dados enviados são inválidos."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Detalhes não encontrados."
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<SerieDetalhes> atualizar(
            @Parameter(
                    description = "ID dos detalhes que serão atualizados",
                    example = "1",
                    required = true
            )
            @PathVariable Long id,
            @Valid @RequestBody SerieDetalhes detalhes) {

        return service.atualizar(id, detalhes)
                .map(detalhesAtualizados -> ResponseEntity.ok(detalhesAtualizados))
                .orElse(ResponseEntity.notFound().build());
    }

    // Exclui os detalhes pelo ID.
    @Operation(
            summary = "Excluir detalhes de série",
            description = "Exclui os detalhes de uma série pelo seu ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Detalhes excluídos com sucesso."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Detalhes não encontrados."
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @Parameter(
                    description = "ID dos detalhes que serão excluídos",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {

        if (service.excluir(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}