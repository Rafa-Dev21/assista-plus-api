package com.assistaplus.assista_plus_api.diretor;

import com.assistaplus.assista_plus_api.serie.SerieController;
import com.assistaplus.assista_plus_api.diretor.dto.DiretorRequestDTO;
import com.assistaplus.assista_plus_api.diretor.dto.DiretorResponseDTO;
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
@RequestMapping("/diretores")
@Tag(name = "Diretores", description = "Endpoints para gerenciamento do catálogo de diretores")
public class DiretorController {

    @Autowired
    private DiretorService service;

    @Operation(
            summary = "Listar diretores",
            description = "Retorna uma lista paginada de diretores cadastrados, utilizando HATEOAS. O tamanho máximo permitido por página é de 50 registros."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de diretores retornada com sucesso.")
    })
    @GetMapping
    public PagedModel<EntityModel<DiretorResponseDTO>> listar(
            @ParameterObject @org.springframework.data.web.PageableDefault(size = 20) Pageable pageable,
            @Parameter(hidden = true) PagedResourcesAssembler<DiretorResponseDTO> assembler) {

        // Trava real de segurança contra abuso de tamanho de página
        if (pageable.getPageSize() > 50) {
            pageable = PageRequest.of(pageable.getPageNumber(), 50, pageable.getSort());
        }

        Page<Diretor> pagina = service.listar(pageable);
        Page<DiretorResponseDTO> paginaDto = pagina.map(d -> new DiretorResponseDTO(d.getId(), d.getNome(), d.getNacionalidade()));

        return assembler.toModel(paginaDto, EntityModel::of);
    }

    @Operation(summary = "Buscar diretores por nome", description = "Pesquisa diretores pelo nome informado.")
    @GetMapping("/buscar")
    public List<DiretorResponseDTO> buscarPorNome(
            @Parameter(description = "Nome usado para pesquisar diretores", example = "Matt", required = true)
            @RequestParam String nome) {
        return service.buscarPorNome(nome).stream()
                .map(d -> new DiretorResponseDTO(d.getId(), d.getNome(), d.getNacionalidade()))
                .collect(Collectors.toList());
    }

    @Operation(
            summary = "Buscar diretor por ID",
            description = "Consulta um diretor pelo seu identificador e retorna seus dados com links HATEOAS."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Diretor encontrado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Diretor não encontrado.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<DiretorResponseDTO>> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(diretor -> {
                    DiretorResponseDTO response = new DiretorResponseDTO(diretor.getId(), diretor.getNome(), diretor.getNacionalidade());
                    EntityModel<DiretorResponseDTO> model = EntityModel.of(response);

                    DiretorRequestDTO mockDto = new DiretorRequestDTO("", "");
                    Pageable mockPageable = PageRequest.of(0, 20);

                    model.add(linkTo(methodOn(DiretorController.class).buscarPorId(id)).withSelfRel());
                    model.add(linkTo(methodOn(DiretorController.class).atualizar(id, mockDto)).withRel("atualizar"));
                    model.add(linkTo(methodOn(DiretorController.class).excluir(id)).withRel("excluir"));
                    model.add(linkTo(methodOn(SerieController.class).listar(mockPageable, null)).withRel("series"));

                    return ResponseEntity.ok(model);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Cadastrar diretor",
            description = "Cadastra um novo diretor após validar os dados enviados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Diretor cadastrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Os dados enviados são inválidos.")
    })
    @PostMapping
    public ResponseEntity<DiretorResponseDTO> cadastrar(@Valid @RequestBody DiretorRequestDTO dto) {
        Diretor diretor = new Diretor();
        diretor.setNome(dto.nome());
        diretor.setNacionalidade(dto.nacionalidade());

        Diretor salvo = service.salvar(diretor);
        DiretorResponseDTO response = new DiretorResponseDTO(salvo.getId(), salvo.getNome(), salvo.getNacionalidade());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Atualizar diretor", description = "Atualiza os dados de um diretor existente pelo ID informado.")
    @PutMapping("/{id}")
    public ResponseEntity<DiretorResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody DiretorRequestDTO dto) {
        Diretor diretor = new Diretor();
        diretor.setNome(dto.nome());
        diretor.setNacionalidade(dto.nacionalidade());

        return service.atualizar(id, diretor)
                .map(d -> ResponseEntity.ok(new DiretorResponseDTO(d.getId(), d.getNome(), d.getNacionalidade())))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Excluir diretor", description = "Exclui um diretor cadastrado pelo seu ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (service.excluir(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
