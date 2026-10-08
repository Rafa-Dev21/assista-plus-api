package com.assistaplus.assista_plus_api.diretor;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springdoc.core.annotations.ParameterObject;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;

// @RestController informa ao Spring que esta classe
// será responsável pelos endpoints REST de Diretor.
@RestController

// Define o endereço base dos endpoints.
@RequestMapping("/diretores")
public class DiretorController {

    // Service responsável pela lógica dos diretores.
    @Autowired
    private DiretorService service;


    // GET /diretores
    //
    // Lista os diretores utilizando paginação.
    //
    // Exemplo:
    // /diretores?page=0&size=10
    @Operation(
            summary = "Listar diretores",
            description = "Retorna uma lista paginada de diretores cadastrados, utilizando HATEOAS."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de diretores retornada com sucesso."
            )
    })
    @GetMapping
    public PagedModel<EntityModel<Diretor>> listar(
            @ParameterObject Pageable pageable,
            @Parameter(hidden = true) PagedResourcesAssembler<Diretor> assembler) {

        Page<Diretor> pagina = service.listar(pageable);

        return assembler.toModel(pagina);
    }


    // GET /diretores/buscar?nome=...
    //
    // Endpoint personalizado para pesquisar diretores pelo nome.
    @Operation(
            summary = "Buscar diretores por nome",
            description = "Pesquisa diretores pelo nome informado."
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
    public List<Diretor> buscarPorNome(@Parameter(
            description = "Nome usado para pesquisar diretores",
            example = "Rafael",
            required = true
    )@RequestParam String nome) {
        return service.buscarPorNome(nome);
    }


    // GET /diretores/{id}
    //
    // Busca um diretor específico pelo ID.
    //
    // Se encontrar → 200 OK.
    // Se não encontrar → 404 Not Found.
    @Operation(
            summary = "Buscar diretor por ID",
            description = "Consulta um diretor pelo seu identificador e retorna seus dados com links HATEOAS."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Diretor encontrado com sucesso."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Diretor não encontrado."
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Diretor>> buscarPorId(@Parameter(
            description = "ID do diretor que será consultado",
            example = "1",
            required = true
    )@PathVariable Long id) {

        return service.buscarPorId(id)
                .map(diretor -> {

                    EntityModel<Diretor> model = EntityModel.of(diretor);

                    // Link para consultar o próprio diretor.
                    model.add(linkTo(methodOn(DiretorController.class)
                            .buscarPorId(id))
                            .withSelfRel());

                    // Link para atualizar o diretor.
                    model.add(linkTo(methodOn(DiretorController.class)
                            .atualizar(id, diretor))
                            .withRel("atualizar"));

                    // Link para excluir o diretor.
                    model.add(linkTo(methodOn(DiretorController.class)
                            .excluir(id))
                            .withRel("excluir"));

                    // Link para consultar as séries relacionadas ao diretor.
                    model.add(linkTo(methodOn(SerieController.class)
                            .listar(null, null))
                            .withRel("series"));

                    return ResponseEntity.ok(model);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    // POST /diretores
    //
    // Cadastra um novo diretor.
    //
    // @Valid ativa as validações definidas na entidade Diretor.
    @Operation(
            summary = "Cadastrar diretor",
            description = "Cadastra um novo diretor após validar os dados enviados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Diretor cadastrado com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Os dados enviados são inválidos."
            )
    })
    @PostMapping
    public Diretor cadastrar(@Valid @RequestBody Diretor diretor) {
        return service.salvar(diretor);
    }


    // PUT /diretores/{id}
    //
    // Atualiza um diretor existente.
    //
    // Se encontrar → 200 OK.
    // Se não encontrar → 404 Not Found.
    @Operation(
            summary = "Atualizar diretor",
            description = "Atualiza os dados de um diretor existente pelo ID informado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Diretor atualizado com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Os dados enviados são inválidos."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Diretor não encontrado."
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Diretor> atualizar( @Parameter(
            description = "ID do diretor que será atualizado",
                                                           example = "1",
                                                          required = true
                                                  )
            @PathVariable Long id,
            @Valid @RequestBody Diretor diretor) {

        return service.atualizar(id, diretor)
                .map(diretorAtualizado -> ResponseEntity.ok(diretorAtualizado))
                .orElse(ResponseEntity.notFound().build());
    }


    // DELETE /diretores/{id}
    //
    // Exclui um diretor.
    //
    // Se encontrar → 204 No Content.
    // Se não encontrar → 404 Not Found.
    @Operation(
            summary = "Excluir diretor",
            description = "Exclui um diretor cadastrado pelo seu ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Diretor excluído com sucesso."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Diretor não encontrado."
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(  @Parameter(
            description = "ID do diretor que será excluído",
            example = "1",
            required = true
    )@PathVariable Long id) {

        if (service.excluir(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}