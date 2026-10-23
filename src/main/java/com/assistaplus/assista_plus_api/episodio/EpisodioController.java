package com.assistaplus.assista_plus_api.episodio;

import com.assistaplus.assista_plus_api.temporada.Temporada;
import com.assistaplus.assista_plus_api.temporada.TemporadaController;
import com.assistaplus.assista_plus_api.temporada.TemporadaRepository;
import com.assistaplus.assista_plus_api.episodio.dto.EpisodioRequestDTO;
import com.assistaplus.assista_plus_api.episodio.dto.EpisodioResponseDTO;
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
@RequestMapping("/episodios")
@Tag(name = "Episódios", description = "Endpoints para gerenciamento de episódios")
public class EpisodioController {

    @Autowired
    private EpisodioService service;

    @Autowired
    private TemporadaRepository temporadaRepository;

    // Método auxiliar para conversão de Entidade para DTO de saída limpa
    private EpisodioResponseDTO toDTO(Episodio e) {
        Long idTemp = e.getTemporada() != null ? e.getTemporada().getId() : null;
        Integer numTemp = e.getTemporada() != null ? e.getTemporada().getNumero() : null;
        return new EpisodioResponseDTO(e.getId(), e.getNumero(), e.getTitulo(), e.getDuracao(), idTemp, numTemp);
    }

    @Operation(summary = "Listar episódios de forma paginada", description = "Retorna os episódios cadastrados. Tamanho máximo: 50 itens por página.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de episódios retornada com sucesso.")
    })
    @GetMapping
    public PagedModel<EntityModel<EpisodioResponseDTO>> listar(
            @ParameterObject @org.springframework.data.web.PageableDefault(size = 20) Pageable pageable,
            @Parameter(hidden = true) PagedResourcesAssembler<EpisodioResponseDTO> assembler) {

        if (pageable.getPageSize() > 50) {
            pageable = PageRequest.of(pageable.getPageNumber(), 50, pageable.getSort());
        }

        Page<Episodio> pagina = service.listar(pageable);
        Page<EpisodioResponseDTO> paginaDto = pagina.map(this::toDTO);

        return assembler.toModel(paginaDto, EntityModel::of);
    }

    @Operation(summary = "Buscar episódios por título", description = "Pesquisa episódios pelo título informado.")
    @GetMapping("/buscar")
    public List<EpisodioResponseDTO> buscarPorTitulo(@RequestParam String titulo) {
        return service.buscarPorTitulo(titulo).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Buscar episódio por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Episódio encontrado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Episódio não encontrado.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<EpisodioResponseDTO>> buscarPorId(@PathVariable Long id) {

        return service.buscarPorId(id)
                .map(episodio -> {
                    EntityModel<EpisodioResponseDTO> model = EntityModel.of(toDTO(episodio));

                    EpisodioRequestDTO mockDto = new EpisodioRequestDTO(1, "", 1, 1L);
                    Pageable mockPageable = PageRequest.of(0, 20);

                    model.add(linkTo(methodOn(EpisodioController.class).buscarPorId(id)).withSelfRel());
                    model.add(linkTo(methodOn(EpisodioController.class).atualizar(id, mockDto)).withRel("atualizar"));
                    model.add(linkTo(methodOn(EpisodioController.class).excluir(id)).withRel("excluir"));
                    model.add(linkTo(methodOn(TemporadaController.class).listar(mockPageable, null)).withRel("temporadas"));

                    return ResponseEntity.ok(model);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Cadastrar episódio", description = "Cadastra um novo episódio vinculando apenas ao ID da temporada correspondente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Episódio cadastrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Os dados enviados são inválidos."),
            @ApiResponse(responseCode = "404", description = "Temporada informada não foi encontrada.")
    })
    @PostMapping
    public ResponseEntity<EpisodioResponseDTO> cadastrar(@Valid @RequestBody EpisodioRequestDTO dto) {
        if (dto.temporadaId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ID da temporada é obrigatório.");
        }

        // Valida se a temporada existe no banco de dados antes de vincular
        Temporada t = temporadaRepository.findById(dto.temporadaId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Temporada com ID " + dto.temporadaId() + " não encontrada."
                ));

        Episodio e = new Episodio();
        e.setNumero(dto.numero());
        e.setTitulo(dto.titulo());
        e.setDuracao(dto.duracao());
        e.setTemporada(t);

        Episodio salvo = service.salvar(e);
        Episodio episodioCompleto = service.buscarPorId(salvo.getId()).orElse(salvo);

        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(episodioCompleto));
    }


    @Operation(summary = "Atualizar episódio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Episódio atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Os dados enviados são inválidos."),
            @ApiResponse(responseCode = "404", description = "Episódio ou Temporada não encontrada.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EpisodioResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody EpisodioRequestDTO dto) {
        if (dto.temporadaId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ID da temporada é obrigatório.");
        }

        // Valida se a temporada existe no banco de dados antes de atualizar
        Temporada t = temporadaRepository.findById(dto.temporadaId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Temporada com ID " + dto.temporadaId() + " não encontrada."
                ));

        Episodio e = new Episodio();
        e.setNumero(dto.numero());
        e.setTitulo(dto.titulo());
        e.setDuracao(dto.duracao());
        e.setTemporada(t);

        return service.atualizar(id, e)
                .map(atualizado -> {
                    Episodio episodioCompleto = service.buscarPorId(atualizado.getId()).orElse(atualizado);
                    return ResponseEntity.ok(toDTO(episodioCompleto));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Excluir episódio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Episódio excluído com sucesso."),
            @ApiResponse(responseCode = "404", description = "Episódio não encontrado.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (service.excluir(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}