package com.assistaplus.assista_plus_api.ator;

import com.assistaplus.assista_plus_api.serie.SerieController;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PatchMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springdoc.core.annotations.ParameterObject;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;

// @RestController informa ao Spring que esta classe
// será responsável por receber e responder requisições HTTP.

// O Controller é a camada responsável por receber as requisições
// e encaminhá-las para o Service.

@RestController
@RequestMapping("/atores")
public class AtorController {

    @Autowired
    private AtorService service;

    // O Controller recebe a requisição GET e envia o Pageable
    // para o Service, permitindo listar os atores com paginação.
    @Operation(
            summary = "Listar atores",
            description = "Retorna uma lista paginada de atores cadastrados, utilizando HATEOAS."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de atores retornada com sucesso."
            )
    })
    @GetMapping
    public PagedModel<EntityModel<Ator>> listar(
            @ParameterObject Pageable pageable,
            @Parameter(hidden = true) PagedResourcesAssembler<Ator> assembler) {

        Page<Ator> pagina = service.listar(pageable);

        return assembler.toModel(pagina);
    }

    // Busca atores pelo nome informado na URL.
    // Exemplo: /atores/buscar?nome=Millie
    @Operation(
            summary = "Buscar atores por nome",
            description = "Pesquisa atores pelo nome informado."
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
    public List<Ator> buscarPorNome( @Parameter(
            description = "Nome usado para pesquisar atores",
            example = "Millie Bobby Brown",
            required = true
    )@RequestParam String nome) {
        return service.buscarPorNome(nome);
    }

    // Utilizamos @PathVariable para receber o ID informado na URL.
    // ResponseEntity permite retornar 200 quando o ator existe
    // e 404 quando o ID não foi encontrado.
    @Operation(
            summary = "Buscar ator por ID",
            description = "Consulta um ator pelo seu identificador e retorna seus dados com links HATEOAS."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ator encontrado com sucesso."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ator não encontrado."
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Ator>> buscarPorId(@Parameter(
            description = "ID do ator que será consultado",
            example = "1",
            required = true
    )@PathVariable Long id) {

        return service.buscarPorId(id)
                .map(ator -> {

                    EntityModel<Ator> model = EntityModel.of(ator);

                    // Link para consultar o próprio ator.
                    model.add(linkTo(methodOn(AtorController.class)
                            .buscarPorId(id))
                            .withSelfRel());

                    // Link para atualizar o ator.
                    model.add(linkTo(methodOn(AtorController.class)
                            .atualizar(id, ator))
                            .withRel("atualizar"));

                    // Link para excluir o ator.
                    model.add(linkTo(methodOn(AtorController.class)
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

    // @PostMapping é utilizado para cadastrar um novo ator.
    // @Valid ativa as validações definidas na entidade.
    @Operation(
            summary = "Cadastrar ator",
            description = "Cadastra um novo ator após validar os dados enviados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ator cadastrado com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Os dados enviados são inválidos."
            )
    })
    @PostMapping
    public Ator cadastrar(@Valid @RequestBody Ator ator) {
        return service.salvar(ator);
    }

    // Utilizamos PUT para atualizar um ator existente.
    // O ID vem pela URL e os novos dados são enviados no corpo.
    @Operation(
            summary = "Atualizar ator",
            description = "Atualiza os dados de um ator existente pelo ID informado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ator atualizado com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Os dados enviados são inválidos."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ator não encontrado."
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Ator> atualizar(@Parameter(
            description = "ID do ator que será atualizado",
                                                      example = "1",
                                                      required = true
                                              )
            @PathVariable Long id,
            @Valid @RequestBody Ator ator) {

        return service.atualizar(id, ator)
                .map(atorAtualizado -> ResponseEntity.ok(atorAtualizado))
                .orElse(ResponseEntity.notFound().build());
    }

    // Quando a exclusão acontece, retornamos 204 No Content.
    // Caso o ator não exista, retornamos 404 Not Found.
    @Operation(
            summary = "Excluir ator",
            description = "Exclui um ator cadastrado pelo seu ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Ator excluído com sucesso."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ator não encontrado."
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@Parameter(
            description = "ID do ator que será excluído",
            example = "1",
            required = true
    )@PathVariable Long id) {

        if (service.excluir(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    // Adiciona uma série existente ao ator.
    // Exemplo: /atores/1/series/1
    @Operation(
            summary = "Adicionar série ao ator",
            description = "Associa uma série existente a um ator existente."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Série associada ao ator com sucesso."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ator ou série não encontrado."
            )
    })
    @PatchMapping("/{atorId}/series/{serieId}")
    public ResponseEntity<Ator> adicionarSerie(
            @PathVariable Long atorId,
            @PathVariable Long serieId) {

        return service.adicionarSerie(atorId, serieId)
                .map(ator -> ResponseEntity.ok(ator))
                .orElse(ResponseEntity.notFound().build());
    }
}