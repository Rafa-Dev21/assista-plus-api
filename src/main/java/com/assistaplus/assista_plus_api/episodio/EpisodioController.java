package com.assistaplus.assista_plus_api.episodio;

import com.assistaplus.assista_plus_api.temporada.TemporadaController;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;

// @RestController informa ao Spring que esta classe
// será responsável por receber e responder requisições HTTP.

// O Controller é a camada responsável por receber as requisições
// e encaminhá-las para o Service.

@RestController
@RequestMapping("/episodios")
public class EpisodioController {

    @Autowired
    private EpisodioService service;

    // O Controller recebe a requisição GET e envia o Pageable
    // para o Service, permitindo listar os episódios com paginação.

    @Operation(
            summary = "Listar episódios",
            description = "Retorna uma lista paginada de episódios cadastrados, utilizando HATEOAS."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de episódios retornada com sucesso."
            )
    })
    @GetMapping
    public PagedModel<EntityModel<Episodio>> listar(
            Pageable pageable,
            PagedResourcesAssembler<Episodio> assembler) {

        Page<Episodio> pagina = service.listar(pageable);

        return assembler.toModel(pagina);
    }

    // Busca episódios pelo título informado na URL.
    @Operation(
            summary = "Buscar episódios por título",
            description = "Pesquisa episódios pelo título informado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Busca realizada com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "O parâmetro título não foi informado."
            )
    })
    @GetMapping("/buscar")
    public List<Episodio> buscarPorTitulo(@Parameter(
            description = "Título ou parte do título do episódio",
            example = "Capítulo Um",
            required = true
    )@RequestParam String titulo) {
        return service.buscarPorTitulo(titulo);
    }

    // Utilizamos @PathVariable para receber o ID informado na URL.
    // ResponseEntity permite retornar 200 quando o episódio existe
    // e 404 quando o ID não foi encontrado.
    @Operation(
            summary = "Buscar episódio por ID",
            description = "Consulta um episódio pelo seu identificador e retorna seus dados com links HATEOAS."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Episódio encontrado com sucesso."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Episódio não encontrado."
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Episodio>> buscarPorId(@Parameter(
            description = "ID do episódio que será consultado",
            example = "1",
            required = true
    )@PathVariable Long id) {

        return service.buscarPorId(id)
                .map(episodio -> {

                    EntityModel<Episodio> model = EntityModel.of(episodio);

                    // Link para consultar o próprio episódio.
                    model.add(linkTo(methodOn(EpisodioController.class)
                            .buscarPorId(id))
                            .withSelfRel());

                    // Link para atualizar o episódio.
                    model.add(linkTo(methodOn(EpisodioController.class)
                            .atualizar(id, episodio))
                            .withRel("atualizar"));

                    // Link para excluir o episódio.
                    model.add(linkTo(methodOn(EpisodioController.class)
                            .excluir(id))
                            .withRel("excluir"));

                    // Link para consultar as temporadas.
                    model.add(linkTo(methodOn(TemporadaController.class)
                            .listar(null, null))
                            .withRel("temporadas"));

                    return ResponseEntity.ok(model);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // @PostMapping é utilizado para cadastrar um novo episódio.
    // @Valid ativa as validações definidas na entidade.

    @Operation(
            summary = "Cadastrar episódio",
            description = "Cadastra um novo episódio após validar os dados enviados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Episódio cadastrado com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Os dados enviados são inválidos."
            )
    })
    @PostMapping
    public Episodio cadastrar(@Valid @RequestBody Episodio episodio) {
        return service.salvar(episodio);
    }

    // Utilizamos PUT para atualizar um episódio existente.
    // O ID vem pela URL e os novos dados são enviados no corpo.
    @Operation(
            summary = "Atualizar episódio",
            description = "Atualiza os dados de um episódio existente pelo ID informado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Episódio atualizado com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Os dados enviados são inválidos."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Episódio não encontrado."
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Episodio> atualizar(
            @Parameter(
                    description = "ID do episódio que será atualizado",
                    example = "1",
                    required = true
            )
            @PathVariable Long id,
            @Valid @RequestBody Episodio episodio) {

        return service.atualizar(id, episodio)
                .map(episodioAtualizado -> ResponseEntity.ok(episodioAtualizado))
                .orElse(ResponseEntity.notFound().build());
    }

    // Quando a exclusão acontece, retornamos 204 No Content.
    // Caso o episódio não exista, retornamos 404 Not Found.
    @Operation(
            summary = "Excluir episódio",
            description = "Exclui um episódio cadastrado pelo seu ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Episódio excluído com sucesso."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Episódio não encontrado."
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(  @Parameter(
            description = "ID do episódio que será excluído",
            example = "1",
            required = true
    )@PathVariable Long id) {

        if (service.excluir(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}