package com.assistaplus.assista_plus_api.avaliacao;

import com.assistaplus.assista_plus_api.serie.Serie;
import com.assistaplus.assista_plus_api.serie.SerieController;
import com.assistaplus.assista_plus_api.serie.SerieRepository;
import com.assistaplus.assista_plus_api.usuario.Usuario;
import com.assistaplus.assista_plus_api.usuario.UsuarioController;
import com.assistaplus.assista_plus_api.usuario.UsuarioRepository;
import com.assistaplus.assista_plus_api.avaliacao.dto.AvaliacaoRequestDTO;
import com.assistaplus.assista_plus_api.avaliacao.dto.AvaliacaoResponseDTO;
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
@RequestMapping("/avaliacoes")
@Tag(name = "Avaliações", description = "Endpoints para gerenciamento de notas e críticas das séries")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoService service;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SerieRepository serieRepository;

    // Método auxiliar para conversão de Entidade para DTO de saída limpa
    private AvaliacaoResponseDTO toDTO(Avaliacao av) {
        Long idUser = av.getUsuario() != null ? av.getUsuario().getId() : null;
        String nomeUser = av.getUsuario() != null ? av.getUsuario().getNome() : null;
        Long idSerie = av.getSerie() != null ? av.getSerie().getId() : null;
        String tituloSerie = av.getSerie() != null ? av.getSerie().getTitulo() : null;

        return new AvaliacaoResponseDTO(av.getId(), av.getNota(), av.getComentario(), idUser, nomeUser, idSerie, tituloSerie);
    }

    @Operation(summary = "Listar críticas e avaliações de forma paginada", description = "Retorna as avaliações do sistema. Tamanho máximo: 50 itens por página.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de avaliações retornada com sucesso.")
    })
    @GetMapping
    public PagedModel<EntityModel<AvaliacaoResponseDTO>> listar(
            @ParameterObject @org.springframework.data.web.PageableDefault(size = 20) Pageable pageable,
            @Parameter(hidden = true) PagedResourcesAssembler<AvaliacaoResponseDTO> assembler) {

        if (pageable.getPageSize() > 50) {
            pageable = PageRequest.of(pageable.getPageNumber(), 50, pageable.getSort());
        }

        Page<Avaliacao> pagina = service.listar(pageable);
        Page<AvaliacaoResponseDTO> paginaDto = pagina.map(this::toDTO);

        return assembler.toModel(paginaDto, EntityModel::of);
    }

    @Operation(summary = "Buscar avaliações por nota", description = "Pesquisa avaliações pela nota informada.")
    @GetMapping("/buscar")
    public List<AvaliacaoResponseDTO> buscarPorNota(@RequestParam Integer nota) {
        return service.buscarPorNota(nota).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Buscar avaliação por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Avaliação encontrada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Avaliação não encontrada.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<AvaliacaoResponseDTO>> buscarPorId(@PathVariable Long id) {

        return service.buscarPorId(id)
                .map(avaliacao -> {
                    EntityModel<AvaliacaoResponseDTO> model = EntityModel.of(toDTO(avaliacao));

                    AvaliacaoRequestDTO mockDto = new AvaliacaoRequestDTO(1, "", 1L, 1L);
                    Pageable mockPageable = PageRequest.of(0, 20);

                    model.add(linkTo(methodOn(AvaliacaoController.class).buscarPorId(id)).withSelfRel());
                    model.add(linkTo(methodOn(AvaliacaoController.class).atualizar(id, mockDto)).withRel("atualizar"));
                    model.add(linkTo(methodOn(AvaliacaoController.class).excluir(id)).withRel("excluir"));
                    model.add(linkTo(methodOn(UsuarioController.class).buscarPorId(1L)).withRel("usuario"));
                    model.add(linkTo(methodOn(SerieController.class).buscarPorId(1L)).withRel("serie"));

                    return ResponseEntity.ok(model);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Cadastrar avaliação de uma série", description = "Registra uma crítica vinculando apenas aos IDs de usuário e série correspondentes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Avaliação cadastrada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Os dados enviados são inválidos (usuário ou série inexistente).")
    })
    @PostMapping
    public ResponseEntity<AvaliacaoResponseDTO> cadastrar(@Valid @RequestBody AvaliacaoRequestDTO dto) {
        if (dto.usuarioId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ID do usuário é obrigatório.");
        }
        if (dto.serieId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ID da série é obrigatório.");
        }

        // 1. Valida existência do Usuário no banco
        Usuario u = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Usuário com ID " + dto.usuarioId() + " não foi encontrado."
                ));

        // 2. Valida existência da Série no banco
        Serie s = serieRepository.findById(dto.serieId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Série com ID " + dto.serieId() + " não foi encontrada."
                ));

        Avaliacao av = new Avaliacao();
        av.setNota(dto.nota());
        av.setComentario(dto.comentario());
        av.setUsuario(u);
        av.setSerie(s);

        Avaliacao salvo = service.salvar(av);

        // Força o Hibernate a recarregar do banco trazendo os nomes e títulos reais das amarrações
        Avaliacao avaliacaoCompleta = service.buscarPorId(salvo.getId()).orElse(salvo);

        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(avaliacaoCompleta));
    }

    @Operation(summary = "Atualizar avaliação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Avaliação atualizada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Os dados enviados são inválidos (usuário ou série inexistente)."),
            @ApiResponse(responseCode = "404", description = "Avaliação não encontrada.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AvaliacaoResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody AvaliacaoRequestDTO dto) {
        if (dto.usuarioId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ID do usuário é obrigatório.");
        }
        if (dto.serieId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ID da série é obrigatório.");
        }

        // 1. Valida existência do Usuário no banco na atualização
        Usuario u = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Usuário com ID " + dto.usuarioId() + " não foi encontrado."
                ));

        // 2. Valida existência da Série no banco na atualização
        Serie s = serieRepository.findById(dto.serieId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Série com ID " + dto.serieId() + " não foi encontrada."
                ));

        Avaliacao av = new Avaliacao();
        av.setNota(dto.nota());
        av.setComentario(dto.comentario());
        av.setUsuario(u);
        av.setSerie(s);

        return service.atualizar(id, av)
                .map(atualizada -> {
                    Avaliacao avaliacaoCompleta = service.buscarPorId(atualizada.getId()).orElse(atualizada);
                    return ResponseEntity.ok(toDTO(avaliacaoCompleta));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Excluir avaliação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Avaliação excluída com sucesso."),
            @ApiResponse(responseCode = "404", description = "Avaliação não encontrada.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (service.excluir(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}