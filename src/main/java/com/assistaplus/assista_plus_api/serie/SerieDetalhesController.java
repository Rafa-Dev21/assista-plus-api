package com.assistaplus.assista_plus_api.serie;

import com.assistaplus.assista_plus_api.serie.dto.SerieDetalhesRequestDTO;
import com.assistaplus.assista_plus_api.serie.dto.SerieDetalhesResponseDTO;
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
@RequestMapping("/serie-detalhes")
@Tag(name = "Série Detalhes", description = "Endpoints para informações técnicas adicionais das séries (Relação 1:1)")
public class SerieDetalhesController {

    @Autowired
    private SerieDetalhesService service;

    @Autowired
    private SerieService sService; // Utilizado para validar a existência e buscar os dados da série

    // Método auxiliar para conversão de Entidade para DTO de saída limpa
    private SerieDetalhesResponseDTO toDTO(SerieDetalhes sd) {
        Long idSerie = sd.getSerie() != null ? sd.getSerie().getId() : null;
        String tituloSerie = sd.getSerie() != null ? sd.getSerie().getTitulo() : null;

        // Correção preventiva: se o objeto série veio do banco incompleto, tenta buscar o título real
        if (tituloSerie == null && idSerie != null) {
            tituloSerie = sService.buscarPorId(idSerie).map(Serie::getTitulo).orElse(null);
        }

        return new SerieDetalhesResponseDTO(
                sd.getId(),
                sd.getIdiomaOriginal(),
                sd.getPaisOrigem(),
                sd.getClassificacaoIndicativa(),
                idSerie,
                tituloSerie
        );
    }

    @Operation(summary = "Listar detalhes técnicos de forma paginada", description = "Retorna os detalhes das séries. Tamanho máximo: 50 itens por página.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de detalhes retornada com sucesso.")
    })
    @GetMapping
    public PagedModel<EntityModel<SerieDetalhesResponseDTO>> listar(
            @ParameterObject @org.springframework.data.web.PageableDefault(size = 20) Pageable pageable,
            @Parameter(hidden = true) PagedResourcesAssembler<SerieDetalhesResponseDTO> assembler) {

        // Trava real de segurança contra abuso de tamanho de página
        if (pageable.getPageSize() > 50) {
            pageable = PageRequest.of(pageable.getPageNumber(), 50, pageable.getSort());
        }

        Page<SerieDetalhes> pagina = service.listar(pageable);
        Page<SerieDetalhesResponseDTO> paginaDto = pagina.map(this::toDTO);

        return assembler.toModel(paginaDto, EntityModel::of);
    }

    @Operation(summary = "Buscar detalhes por país de origem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso.")
    })
    @GetMapping("/buscar")
    public List<SerieDetalhesResponseDTO> buscarPorPais(@RequestParam String paisOrigem) {
        return service.buscarPorPais(paisOrigem).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Buscar detalhes por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalhes encontrados com sucesso."),
            @ApiResponse(responseCode = "404", description = "Detalhes não encontrados.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<SerieDetalhesResponseDTO>> buscarPorId(@PathVariable Long id) {

        return service.buscarPorId(id)
                .map(detalhes -> {
                    EntityModel<SerieDetalhesResponseDTO> model = EntityModel.of(toDTO(detalhes));

                    SerieDetalhesRequestDTO mockDto = new SerieDetalhesRequestDTO("", "", 0, 0L);

                    model.add(linkTo(methodOn(SerieDetalhesController.class).buscarPorId(id)).withSelfRel());
                    model.add(linkTo(methodOn(SerieDetalhesController.class).atualizar(id, mockDto)).withRel("atualizar"));
                    model.add(linkTo(methodOn(SerieDetalhesController.class).excluir(id)).withRel("excluir"));

                    if (detalhes.getSerie() != null) {
                        model.add(linkTo(methodOn(SerieController.class).buscarPorId(detalhes.getSerie().getId())).withRel("serie"));
                    }

                    return ResponseEntity.ok(model);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Cadastrar especificações da série", description = "Cadastra os detalhes técnicos vinculando apenas ao ID da série correspondente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Detalhes cadastrados com sucesso."),
            @ApiResponse(responseCode = "400", description = "Os dados enviados são inválidos (série inexistente ou id ausente).")
    })
    @PostMapping
    public ResponseEntity<SerieDetalhesResponseDTO> cadastrar(@Valid @RequestBody SerieDetalhesRequestDTO dto) {
        if (dto.serieId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ID da série é obrigatório.");
        }

        // Valida se a série realmente existe antes de salvar
        Serie serie = sService.buscarPorId(dto.serieId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Série com ID " + dto.serieId() + " não foi encontrada."
                ));

        SerieDetalhes sd = new SerieDetalhes();
        sd.setIdiomaOriginal(dto.idiomaOriginal());
        sd.setPaisOrigem(dto.paisOrigem());
        sd.setClassificacaoIndicativa(dto.classificacaoIndicativa());
        sd.setSerie(serie);

        SerieDetalhes salva = service.salvar(sd);

        // Força a recarga do banco para vir o título da série completo
        SerieDetalhes detalhesCompletos = service.buscarPorId(salva.getId()).orElse(salva);

        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(detalhesCompletos));
    }

    @Operation(summary = "Atualizar detalhes de série")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalhes atualizados com sucesso."),
            @ApiResponse(responseCode = "400", description = "Os dados enviados são inválidos (série inexistente)."),
            @ApiResponse(responseCode = "404", description = "Detalhes não encontrados.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<SerieDetalhesResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody SerieDetalhesRequestDTO dto) {
        if (dto.serieId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ID da série é obrigatório.");
        }

        // Valida se a série realmente existe na atualização
        Serie serie = sService.buscarPorId(dto.serieId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Série com ID " + dto.serieId() + " não foi encontrada."
                ));

        SerieDetalhes sd = new SerieDetalhes();
        sd.setIdiomaOriginal(dto.idiomaOriginal());
        sd.setPaisOrigem(dto.paisOrigem());
        sd.setClassificacaoIndicativa(dto.classificacaoIndicativa());
        sd.setSerie(serie);

        return service.atualizar(id, sd)
                .map(atualizados -> {
                    SerieDetalhes detalhesCompletos = service.buscarPorId(atualizados.getId()).orElse(atualizados);
                    return ResponseEntity.ok(toDTO(detalhesCompletos));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Excluir detalhes de série")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Detalhes excluídos com sucesso."),
            @ApiResponse(responseCode = "404", description = "Detalhes não encontrados.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (service.excluir(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}