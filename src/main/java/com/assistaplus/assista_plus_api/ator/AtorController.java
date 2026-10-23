package com.assistaplus.assista_plus_api.ator;

import com.assistaplus.assista_plus_api.serie.SerieController;
import com.assistaplus.assista_plus_api.ator.dto.AtorRequestDTO;
import com.assistaplus.assista_plus_api.ator.dto.AtorResponseDTO;
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
@RequestMapping("/atores")
@Tag(name = "Atores", description = "Endpoints para gerenciamento do elenco de atores")
public class AtorController {

    @Autowired
    private AtorService service;

    @Operation(
            summary = "Listar atores",
            description = "Retorna uma lista paginada de atores cadastrados, utilizando HATEOAS. O tamanho máximo permitido por página é de 50 registros."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de atores retornada com sucesso.")
    })
    @GetMapping
    public PagedModel<EntityModel<AtorResponseDTO>> listar(
            @ParameterObject @org.springframework.data.web.PageableDefault(size = 20) Pageable pageable,
            @Parameter(hidden = true) PagedResourcesAssembler<AtorResponseDTO> assembler) {

        // Trava real de segurança de paginação
        if (pageable.getPageSize() > 50) {
            pageable = PageRequest.of(pageable.getPageNumber(), 50, pageable.getSort());
        }

        Page<Ator> pagina = service.listar(pageable);
        Page<AtorResponseDTO> paginaDto = pagina.map(a -> new AtorResponseDTO(a.getId(), a.getNome(), a.getNacionalidade()));

        return assembler.toModel(paginaDto, EntityModel::of);
    }

    @Operation(summary = "Buscar atores por nome", description = "Pesquisa atores pelo nome informado.")
    @GetMapping("/buscar")
    public List<AtorResponseDTO> buscarPorNome(
            @Parameter(description = "Nome usado para pesquisar atores", example = "Millie", required = true)
            @RequestParam String nome) {
        return service.buscarPorNome(nome).stream()
                .map(a -> new AtorResponseDTO(a.getId(), a.getNome(), a.getNacionalidade()))
                .collect(Collectors.toList());
    }

    @Operation(
            summary = "Buscar ator por ID",
            description = "Consulta um actor pelo seu identificador e retorna seus dados com links HATEOAS."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ator encontrado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Ator não encontrado.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<AtorResponseDTO>> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ator -> {
                    AtorResponseDTO response = new AtorResponseDTO(ator.getId(), ator.getNome(), ator.getNacionalidade());
                    EntityModel<AtorResponseDTO> model = EntityModel.of(response);

                    AtorRequestDTO mockDto = new AtorRequestDTO("", "");
                    Pageable mockPageable = PageRequest.of(0, 20);

                    model.add(linkTo(methodOn(AtorController.class).buscarPorId(id)).withSelfRel());
                    model.add(linkTo(methodOn(AtorController.class).atualizar(id, mockDto)).withRel("atualizar"));
                    model.add(linkTo(methodOn(AtorController.class).excluir(id)).withRel("excluir"));
                    model.add(linkTo(methodOn(SerieController.class).listar(mockPageable, null)).withRel("series"));

                    return ResponseEntity.ok(model);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Cadastrar ator",
            description = "Cadastra um novo ator após validar os dados enviados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ator cadastrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Os dados enviados são inválidos.")
    })
    @PostMapping
    public ResponseEntity<AtorResponseDTO> cadastrar(@Valid @RequestBody AtorRequestDTO dto) {
        Ator ator = new Ator();
        ator.setNome(dto.nome());
        ator.setNacionalidade(dto.nacionalidade());

        Ator salvo = service.salvar(ator);
        AtorResponseDTO response = new AtorResponseDTO(salvo.getId(), salvo.getNome(), salvo.getNacionalidade());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Atualizar ator", description = "Atualiza os dados de um ator existente pelo ID informado.")
    @PutMapping("/{id}")
    public ResponseEntity<AtorResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody AtorRequestDTO dto) {
        Ator ator = new Ator();
        ator.setNome(dto.nome());
        ator.setNacionalidade(dto.nacionalidade());

        return service.atualizar(id, ator)
                .map(a -> ResponseEntity.ok(new AtorResponseDTO(a.getId(), a.getNome(), a.getNacionalidade())))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Excluir actor", description = "Exclui um ator cadastrado pelo seu ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (service.excluir(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
