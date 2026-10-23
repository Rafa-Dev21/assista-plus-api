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
import io.swagger.v3.oas.annotations.media.Schema;
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
            @ApiResponse(responseCode = "200", description = "Lista de gêneros retornada com sucesso.")
    })
    @GetMapping
    public PagedModel<EntityModel<GeneroResponseDTO>> listar(
            @ParameterObject @org.springframework.data.web.PageableDefault(size = 20) Pageable pageable, // Mantém apenas o size padrão
            @Parameter(hidden = true) PagedResourcesAssembler<GeneroResponseDTO> assembler) {

        // 🔥 TRAVA REAL NO CÓDIGO: Se o tamanho pedido for maior que 50, força o Spring a cortar para 50
        if (pageable.getPageSize() > 50) {
            pageable = org.springframework.data.domain.PageRequest.of(
                    pageable.getPageNumber(),
                    50,
                    pageable.getSort()
            );
        }

        Page<Genero> pagina = service.listar(pageable);

        // Converte a página de Entidades para uma página de DTOs
        Page<GeneroResponseDTO> paginaDto = pagina.map(g -> new GeneroResponseDTO(g.getId(), g.getNome()));

        return assembler.toModel(paginaDto, EntityModel::of);
    }


    @Operation(summary = "Buscar gêneros por nome", description = "Pesquisa gêneros pelo nome informado.")
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
            @ApiResponse(responseCode = "404", description = "Gênero não encontrado.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<GeneroResponseDTO>> buscarPorId(@PathVariable Long id) {

        return service.buscarPorId(id)
                .map(genero -> {
                    GeneroResponseDTO response = new GeneroResponseDTO(genero.getId(), genero.getNome());
                    EntityModel<GeneroResponseDTO> model = EntityModel.of(response);

                    // Cria instâncias vazias/válidas para os métodos de link evitarem o erro de argumento nulo
                    GeneroRequestDTO mockDto = new GeneroRequestDTO("");
                    Pageable mockPageable = PageRequest.of(0, 20);

                    model.add(linkTo(methodOn(GeneroController.class).buscarPorId(id)).withSelfRel());
                    model.add(linkTo(methodOn(GeneroController.class).atualizar(id, mockDto)).withRel("atualizar")); // CORRIGE: Passa o mockDto válido
                    model.add(linkTo(methodOn(GeneroController.class).excluir(id)).withRel("excluir"));
                    model.add(linkTo(methodOn(SerieController.class).listar(mockPageable, null)).withRel("series")); // CORRIGE: Passa o mockPageable estruturado no lugar do null

                    return ResponseEntity.ok(model);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Cadastrar gênero",
            description = "Cadastra um novo gênero após validar os dados enviados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Gênero cadastrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Os dados enviados são inválidos.")
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
    @PutMapping("/{id}")
    public ResponseEntity<GeneroResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody GeneroRequestDTO dto) {
        Genero genero = new Genero();
        genero.setNome(dto.nome());

        return service.atualizar(id, genero) // CORRIGE: Removeu a chamada fantasma ao método clear() do service
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
