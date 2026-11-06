package com.assistaplus.assista_plus_api.genero;

import com.assistaplus.assista_plus_api.serie.SerieController;
import com.assistaplus.assista_plus_api.genero.dto.GeneroRequestDTO;
import com.assistaplus.assista_plus_api.genero.dto.GeneroResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/generos")
@Tag(name = "Gêneros", description = "Endpoints para gerenciamento de gêneros cinematográficos")
public class GeneroController {

    @Autowired
    private GeneroService service;

    @Operation(
            summary = "Listar gêneros",
            description = "Retorna uma lista paginada de gêneros cadastrados, utilizando HATEOAS. O tamanho máximo permitido por página é de 50 registros."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de gêneros retornada com sucesso."),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido (Too Many Requests). Máximo de 15 requisições por minuto por IP.")
    })
    @GetMapping
    public PagedModel<EntityModel<GeneroResponseDTO>> listar(
            @ParameterObject @org.springframework.data.web.PageableDefault(size = 20) Pageable pageable,
            @Parameter(hidden = true) PagedResourcesAssembler<GeneroResponseDTO> assembler) {

        if (pageable.getPageSize() > 50) {
            pageable = PageRequest.of(pageable.getPageNumber(), 50, pageable.getSort());
        }

        Page<Genero> pagina = service.listar(pageable);
        Page<GeneroResponseDTO> paginaDto = pagina.map(g -> new GeneroResponseDTO(g.getId(), g.getNome()));

        return assembler.toModel(paginaDto, EntityModel::of);
    }

    @Operation(summary = "Buscar gêneros por nome", description = "Pesquisa gêneros pelo nome informado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso."),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido (Too Many Requests). Máximo de 15 requisições por minuto por IP.")
    })
    @GetMapping("/buscar")
    public List<GeneroResponseDTO> buscarPorNome(
            @Parameter(description = "Nome usado na pesquisa", example = "Terror", required = true)
            @RequestParam String nome) {
        return service.buscarPorNome(nome).stream()
                .map(g -> new GeneroResponseDTO(g.getId(), g.getNome()))
                .collect(Collectors.toList());
    }

    @Operation(
            summary = "Buscar gênero por ID",
            description = "Consulta um gênero pelo seu identificador e retorna seus dados com links HATEOAS."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gênero encontrado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Gênero não encontrado."),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido (Too Many Requests). Máximo de 15 requisições por minuto por IP.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<GeneroResponseDTO>> buscarPorId(@PathVariable Long id) {

        return service.buscarPorId(id)
                .map(genero -> {
                    GeneroResponseDTO response = new GeneroResponseDTO(genero.getId(), genero.getNome());
                    EntityModel<GeneroResponseDTO> model = EntityModel.of(response);

                    GeneroRequestDTO mockDto = new GeneroRequestDTO("");
                    Pageable mockPageable = PageRequest.of(0, 20);

                    model.add(linkTo(methodOn(GeneroController.class).buscarPorId(id)).withSelfRel());
                    model.add(linkTo(methodOn(GeneroController.class).atualizar(id, mockDto)).withRel("atualizar"));
                    model.add(linkTo(methodOn(GeneroController.class).excluir(id)).withRel("excluir"));
                    model.add(linkTo(methodOn(SerieController.class).listar(mockPageable, null)).withRel("series"));

                    return ResponseEntity.ok(model);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Cadastrar gênero",
            description = "Cadastra um novo gênero após validar os dados enviados e verificar a chave de idempotência."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Gênero cadastrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Os dados enviados são inválidos."),
            @ApiResponse(responseCode = "409", description = "Conflito: Já existe um gênero cadastrado com este nome."),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity: A chave de idempotência informada no cabeçalho X-Idempotency-Key já foi utilizada com dados de requisição diferentes (payload divergente)."),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido (Too Many Requests). Máximo de 15 requisições por minuto por IP.")
    })
    @PostMapping
    public ResponseEntity<GeneroResponseDTO> cadastrar(@Valid @RequestBody GeneroRequestDTO dto) {
        Genero genero = new Genero();
        genero.setNome(dto.nome());

        Genero salvo = service.salvar(genero);
        GeneroResponseDTO response = new GeneroResponseDTO(salvo.getId(), salvo.getNome());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Atualizar gênero", description = "Atualiza os dados de um gênero existente pelo ID informado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gênero atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Os dados enviados são inválidos."),
            @ApiResponse(responseCode = "404", description = "Gênero não encontrado."),
            @ApiResponse(responseCode = "409", description = "Conflito: Já existe outro gênero cadastrado com este nome.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<GeneroResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody GeneroRequestDTO dto) {
        Genero genero = new Genero();
        genero.setNome(dto.nome());

        return service.atualizar(id, genero)
                .map(g -> ResponseEntity.ok(new GeneroResponseDTO(g.getId(), g.getNome())))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Excluir gênero", description = "Exclui um gênero cadastrado pelo seu ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (service.excluir(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}