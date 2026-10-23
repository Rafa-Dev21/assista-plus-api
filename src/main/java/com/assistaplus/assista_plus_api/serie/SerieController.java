package com.assistaplus.assista_plus_api.serie;

import com.assistaplus.assista_plus_api.diretor.Diretor;
import com.assistaplus.assista_plus_api.diretor.DiretorController;
import com.assistaplus.assista_plus_api.diretor.DiretorRepository;
import com.assistaplus.assista_plus_api.genero.Genero;
import com.assistaplus.assista_plus_api.genero.GeneroRepository;
import com.assistaplus.assista_plus_api.serie.dto.SerieRequestDTO;
import com.assistaplus.assista_plus_api.serie.dto.SerieResponseDTO;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/series")
@Tag(name = "Séries", description = "Endpoints para gerenciamento do catálogo de séries")
public class SerieController {

    @Autowired
    private SerieService service;

    @Autowired
    private GeneroRepository generoRepository;

    @Autowired
    private DiretorRepository diretorRepository;

    // Método auxiliar para converter a Entidade do banco para o DTO de resposta do catálogo
    private SerieResponseDTO toDTO(Serie s) {
        String diretor = s.getDiretor() != null ? s.getDiretor().getNome() : null;
        List<String> generos = s.getGeneros().stream().map(Genero::getNome).collect(Collectors.toList());
        return new SerieResponseDTO(
                s.getId(),
                s.getTitulo(),
                s.getDescricao(),
                s.getAnoLancamento(),
                s.getImagemUrl(),
                s.getStatus() != null ? s.getStatus().name() : null,
                diretor,
                generos
        );
    }

    @Operation(summary = "Listar séries de forma paginada", description = "Retorna o catálogo de séries. Tamanho máximo: 50 itens por página.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de séries retornada com sucesso.")
    })
    @GetMapping
    public PagedModel<EntityModel<SerieResponseDTO>> listar(
            @ParameterObject @org.springframework.data.web.PageableDefault(size = 20) Pageable pageable,
            @Parameter(hidden = true) PagedResourcesAssembler<SerieResponseDTO> assembler) {

        if (pageable.getPageSize() > 50) {
            pageable = PageRequest.of(pageable.getPageNumber(), 50, pageable.getSort());
        }

        Page<SerieResponseDTO> dtoPage = service.listar(pageable).map(this::toDTO);
        return assembler.toModel(dtoPage, EntityModel::of);
    }

    @Operation(summary = "Buscar série por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Série encontrada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Série não encontrada.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<SerieResponseDTO>> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(s -> {
                    EntityModel<SerieResponseDTO> model = EntityModel.of(toDTO(s));
                    model.add(linkTo(methodOn(SerieController.class).buscarPorId(id)).withSelfRel());
                    if (s.getDiretor() != null) {
                        model.add(linkTo(methodOn(DiretorController.class).buscarPorId(s.getDiretor().getId())).withRel("diretor"));
                    }
                    return ResponseEntity.ok(model);
                }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar séries por título")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso.")
    })
    @GetMapping("/buscar")
    public List<SerieResponseDTO> buscarPorTitle(@RequestParam String titulo) {
        return service.buscarPorTitulo(titulo).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Operation(summary = "Cadastrar série", description = "Cadastra uma nova série vinculando o link da imagem, IDs de diretores e gêneros.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Série cadastrada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Os dados enviados são inválidos (diretor ou gênero inexistente).")
    })
    @PostMapping
    public ResponseEntity<SerieResponseDTO> cadastrar(@Valid @RequestBody SerieRequestDTO dto) {
        Serie s = new Serie();
        s.setTitulo(dto.titulo());
        s.setDescricao(dto.descricao());
        s.setAnoLancamento(dto.anoLancamento());
        s.setImagemUrl(dto.imagemUrl());

        if (dto.status() != null) {
            s.setStatus(StatusSerie.valueOf(dto.status()));
        }

        // 1. Valida e busca o Diretor no banco
        if (dto.diretorId() != null) {
            Diretor diretor = diretorRepository.findById(dto.diretorId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Diretor com ID " + dto.diretorId() + " não foi encontrado."
                    ));
            s.setDiretor(diretor);
        }

        // 2. Valida e busca os Gêneros no banco
        if (dto.generosIds() != null && !dto.generosIds().isEmpty()) {
            List<Genero> generosEncontrados = generoRepository.findAllById(dto.generosIds());

            // Se a quantidade de gêneros encontrados no banco for menor do que os IDs enviados, algum ID não existe
            if (generosEncontrados.size() != dto.generosIds().size()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Um ou mais IDs de gêneros informados não existem no banco de dados."
                );
            }
            s.setGeneros(new HashSet<>(generosEncontrados));
        }

        Serie salva = service.salvar(s);
        Serie serieCompleta = service.buscarPorId(salva.getId()).orElse(salva);

        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(serieCompleta));
    }

    @Operation(summary = "Atualizar série")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Série atualizada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Os dados enviados são inválidos (diretor ou gênero inexistente)."),
            @ApiResponse(responseCode = "404", description = "Série não encontrada.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<SerieResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody SerieRequestDTO dto) {
        Serie s = new Serie();
        s.setTitulo(dto.titulo());
        s.setDescricao(dto.descricao());
        s.setAnoLancamento(dto.anoLancamento());
        s.setImagemUrl(dto.imagemUrl());

        if (dto.status() != null) {
            s.setStatus(StatusSerie.valueOf(dto.status()));
        }

        // 1. Valida e busca o Diretor no banco na atualização
        if (dto.diretorId() != null) {
            Diretor diretor = diretorRepository.findById(dto.diretorId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Diretor com ID " + dto.diretorId() + " não foi encontrado."
                    ));
            s.setDiretor(diretor);
        }

        // 2. Valida e busca os Gêneros no banco na atualização
        if (dto.generosIds() != null && !dto.generosIds().isEmpty()) {
            List<Genero> generosEncontrados = generoRepository.findAllById(dto.generosIds());

            if (generosEncontrados.size() != dto.generosIds().size()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Um ou mais IDs de gêneros informados não existem no banco de dados."
                );
            }
            s.setGeneros(new HashSet<>(generosEncontrados));
        }

        return service.atualizar(id, s)
                .map(atualizada -> {
                    Serie serieCompleta = service.buscarPorId(atualizada.getId()).orElse(atualizada);
                    return ResponseEntity.ok(toDTO(serieCompleta));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Excluir série")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Série excluída com sucesso."),
            @ApiResponse(responseCode = "404", description = "Série não encontrada.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        return service.excluir(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}