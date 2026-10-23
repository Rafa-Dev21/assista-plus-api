package com.assistaplus.assista_plus_api.temporada;

import com.assistaplus.assista_plus_api.serie.Serie;
import com.assistaplus.assista_plus_api.serie.SerieController;
import com.assistaplus.assista_plus_api.serie.SerieRepository;
import com.assistaplus.assista_plus_api.temporada.dto.TemporadaRequestDTO;
import com.assistaplus.assista_plus_api.temporada.dto.TemporadaResponseDTO;
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
import org.springframework.web.server.ResponseStatusException;
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
@RequestMapping("/temporadas")
@Tag(name = "Temporadas", description = "Endpoints para gerenciamento de temporadas")
public class TemporadaController {

    @Autowired
    private TemporadaService service;

    @Autowired
    private SerieRepository serieRepository;

    // Método auxiliar para conversão de Entidade para DTO de saída limpa
    private TemporadaResponseDTO toDTO(Temporada t) {
        Long idSerie = t.getSerie() != null ? t.getSerie().getId() : null;
        String tituloSerie = t.getSerie() != null ? t.getSerie().getTitulo() : null;
        return new TemporadaResponseDTO(t.getId(), t.getNumero(), idSerie, tituloSerie);
    }

    @Operation(summary = "Listar temporadas de forma paginada", description = "Retorna as temporadas cadastradas. Tamanho máximo: 50 itens por página.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de temporadas retornada com sucesso.")
    })
    @GetMapping
    public PagedModel<EntityModel<TemporadaResponseDTO>> listar(
            @ParameterObject @org.springframework.data.web.PageableDefault(size = 20) Pageable pageable,
            @Parameter(hidden = true) PagedResourcesAssembler<TemporadaResponseDTO> assembler) {

        // Trava real de segurança contra abuso de tamanho de página
        if (pageable.getPageSize() > 50) {
            pageable = PageRequest.of(pageable.getPageNumber(), 50, pageable.getSort());
        }

        Page<Temporada> pagina = service.listar(pageable);
        Page<TemporadaResponseDTO> paginaDto = pagina.map(this::toDTO);

        return assembler.toModel(paginaDto, EntityModel::of);
    }

    @Operation(summary = "Buscar temporadas por número", description = "Pesquisa temporadas pelo número informado.")
    @GetMapping("/buscar")
    public List<TemporadaResponseDTO> buscarPorNumero(@RequestParam Integer numero) {
        return service.buscarPorNumero(numero).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Buscar temporada por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Temporada encontrada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Temporada não encontrada.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<TemporadaResponseDTO>> buscarPorId(@PathVariable Long id) {

        return service.buscarPorId(id)
                .map(temporada -> {
                    EntityModel<TemporadaResponseDTO> model = EntityModel.of(toDTO(temporada));

                    TemporadaRequestDTO mockDto = new TemporadaRequestDTO(1, 1L);
                    Pageable mockPageable = PageRequest.of(0, 20);

                    model.add(linkTo(methodOn(TemporadaController.class).buscarPorId(id)).withSelfRel());
                    model.add(linkTo(methodOn(TemporadaController.class).atualizar(id, mockDto)).withRel("atualizar"));
                    model.add(linkTo(methodOn(TemporadaController.class).excluir(id)).withRel("excluir"));
                    model.add(linkTo(methodOn(SerieController.class).listar(mockPageable, null)).withRel("series"));

                    return ResponseEntity.ok(model);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Cadastrar temporada", description = "Cadastra uma nova temporada vinculando apenas ao ID da série correspondente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Temporada cadastrada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Os dados enviados são inválidos (erro de validação ou série inexistente).")
    })
    @PostMapping
    public ResponseEntity<TemporadaResponseDTO> cadastrar(@Valid @RequestBody TemporadaRequestDTO dto) {
        if (dto.serieId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ID da série é obrigatório.");
        }

        // Valida se a série realmente existe no banco de dados
        Serie s = serieRepository.findById(dto.serieId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Série com ID " + dto.serieId() + " não foi encontrada."
                ));

        Temporada t = new Temporada();
        t.setNumero(dto.numero());
        t.setSerie(s);

        Temporada salva = service.salvar(t);

        // Recarrega o registro do banco
        Temporada temporadaCompleta = service.buscarPorId(salva.getId()).orElse(salva);

        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(temporadaCompleta));
    }

    @Operation(summary = "Atualizar temporada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Temporada atualizada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Os dados enviados são inválidos (série inexistente)."),
            @ApiResponse(responseCode = "404", description = "Temporada não encontrada.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TemporadaResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody TemporadaRequestDTO dto) {
        if (dto.serieId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ID da série é obrigatório.");
        }

        // Valida se a série realmente existe no banco de dados na atualização
        Serie s = serieRepository.findById(dto.serieId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Série com ID " + dto.serieId() + " não foi encontrada."
                ));

        Temporada t = new Temporada();
        t.setNumero(dto.numero());
        t.setSerie(s);

        return service.atualizar(id, t)
                .map(atualizada -> {
                    Temporada temporadaCompleta = service.buscarPorId(atualizada.getId()).orElse(atualizada);
                    return ResponseEntity.ok(toDTO(temporadaCompleta));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Excluir temporada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Temporada excluída com sucesso."),
            @ApiResponse(responseCode = "404", description = "Temporada não encontrada.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (service.excluir(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}