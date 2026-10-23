package com.assistaplus.assista_plus_api.usuario;

import com.assistaplus.assista_plus_api.avaliacao.AvaliacaoController;
import com.assistaplus.assista_plus_api.usuario.dto.UsuarioRequestDTO;
import com.assistaplus.assista_plus_api.usuario.dto.UsuarioResponseDTO;
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
@RequestMapping("/usuarios")
@Tag(name = "Usuários", description = "Endpoints para gerenciamento de perfis de usuários")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @Operation(
            summary = "Listar usuários",
            description = "Retorna os usuários cadastrados em páginas, utilizando HATEOAS. O tamanho máximo permitido por página é de 50 registros."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista paginada de usuários retornada com sucesso."),
            @ApiResponse(responseCode = "500", description = "Erro interno ao consultar os usuários.")
    })
    @GetMapping
    public PagedModel<EntityModel<UsuarioResponseDTO>> listar(
            @ParameterObject @org.springframework.data.web.PageableDefault(size = 20) Pageable pageable,
            @Parameter(hidden = true) PagedResourcesAssembler<UsuarioResponseDTO> assembler) {

        // Trava real de segurança contra abuso de tamanho de página
        if (pageable.getPageSize() > 50) {
            pageable = PageRequest.of(pageable.getPageNumber(), 50, pageable.getSort());
        }

        Page<Usuario> pagina = service.listar(pageable);
        Page<UsuarioResponseDTO> paginaDto = pagina.map(u -> new UsuarioResponseDTO(u.getId(), u.getNome(), u.getEmail()));

        return assembler.toModel(paginaDto, EntityModel::of);
    }

    @Operation(
            summary = "Buscar usuário por ID",
            description = "Consulta os dados de um usuário cadastrado pelo seu identificador. A resposta inclui links HATEOAS para consultar, atualizar, excluir o usuário e acessar suas avaliações."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Não existe usuário cadastrado com o ID informado.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UsuarioResponseDTO>> buscarPorId(@PathVariable Long id) {

        return service.buscarPorId(id)
                .map(usuario -> {
                    UsuarioResponseDTO response = new UsuarioResponseDTO(usuario.getId(), usuario.getNome(), usuario.getEmail());
                    EntityModel<UsuarioResponseDTO> model = EntityModel.of(response);

                    // Mock atualizado com 3 parâmetros correspondendo ao novo DTO
                    UsuarioRequestDTO mockDto = new UsuarioRequestDTO("", "", "");
                    Pageable mockPageable = PageRequest.of(0, 20);

                    model.add(linkTo(methodOn(UsuarioController.class).buscarPorId(id)).withSelfRel());
                    model.add(linkTo(methodOn(UsuarioController.class).atualizar(id, mockDto)).withRel("atualizar"));
                    model.add(linkTo(methodOn(UsuarioController.class).excluir(id)).withRel("excluir"));
                    model.add(linkTo(methodOn(AvaliacaoController.class).listar(mockPageable, null)).withRel("avaliacoes"));

                    return ResponseEntity.ok(model);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar usuários por nome", description = "Pesquisa usuários pelo nome informado no parâmetro da URL.")
    @GetMapping("/buscar")
    public List<UsuarioResponseDTO> buscarPorNome(@RequestParam String nome) {
        return service.buscarPorNome(nome).stream()
                .map(u -> new UsuarioResponseDTO(u.getId(), u.getNome(), u.getEmail()))
                .collect(Collectors.toList());
    }

    @Operation(summary = "Cadastrar usuário", description = "Cadastra um novo usuário após validar os dados enviados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados enviados são inválidos (ex: e-mail inválido ou senha curta)."),
            @ApiResponse(responseCode = "409", description = "Conflito: Já existe um usuário cadastrado com este e-mail.")
    })
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@Valid @RequestBody UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(dto.senha());

        Usuario salvo = service.salvar(usuario);
        UsuarioResponseDTO response = new UsuarioResponseDTO(salvo.getId(), salvo.getNome(), salvo.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente pelo ID informado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados enviados são inválidos."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado."),
            @ApiResponse(responseCode = "409", description = "Conflito: Este e-mail já está em uso por outro usuário.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(dto.senha());

        return service.atualizar(id, usuario)
                .map(u -> ResponseEntity.ok(new UsuarioResponseDTO(u.getId(), u.getNome(), u.getEmail())))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Excluir usuário", description = "Exclui um usuário cadastrado pelo seu ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (service.excluir(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}