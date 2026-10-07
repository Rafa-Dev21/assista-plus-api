package com.assistaplus.assista_plus_api.usuario;

import com.assistaplus.assista_plus_api.avaliacao.AvaliacaoController;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    // Lista todos os usuários com paginação e HATEOAS.

    @Operation(
            summary = "Listar usuários",
            description = "Retorna os usuários cadastrados em páginas, com links HATEOAS."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista paginada de usuários retornada com sucesso."),
            @ApiResponse(responseCode = "500", description = "Erro interno ao consultar os usuários.")
    })
    @GetMapping
    public PagedModel<EntityModel<Usuario>> listar(
            Pageable pageable,
            PagedResourcesAssembler<Usuario> assembler) {

        Page<Usuario> pagina = service.listar(pageable);

        return assembler.toModel(pagina);
    }

    // Busca um usuário pelo ID.
    @Operation(
            summary = "Buscar usuário por ID",
            description = "Consulta os dados de um usuário cadastrado pelo seu identificador. "
                    + "A resposta inclui links HATEOAS para consultar, atualizar, excluir "
                    + "o usuário e acessar suas avaliações."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário encontrado com sucesso."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Não existe usuário cadastrado com o ID informado."
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> buscarPorId(@PathVariable Long id) {

        return service.buscarPorId(id)
                .map(usuario -> {

                    EntityModel<Usuario> model = EntityModel.of(usuario);

                    // Link para consultar o próprio usuário.
                    model.add(linkTo(methodOn(UsuarioController.class)
                            .buscarPorId(id))
                            .withSelfRel());

                    // Link para atualizar o usuário.
                    model.add(linkTo(methodOn(UsuarioController.class)
                            .atualizar(id, usuario))
                            .withRel("atualizar"));

                    // Link para excluir o usuário.
                    model.add(linkTo(methodOn(UsuarioController.class)
                            .excluir(id))
                            .withRel("excluir"));

                    // Link para consultar as avaliações.
                    model.add(linkTo(methodOn(AvaliacaoController.class)
                            .listar(null, null))
                            .withRel("avaliacoes"));

                    return ResponseEntity.ok(model);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Busca usuários pelo nome.

    @Operation(
            summary = "Buscar usuários por nome",
            description = "Pesquisa usuários pelo nome informado no parâmetro da URL."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso."),
            @ApiResponse(responseCode = "400", description = "O parâmetro nome não foi informado.")
    })
    @GetMapping("/buscar")
    public List<Usuario> buscarPorNome(@RequestParam String nome) {
        return service.buscarPorNome(nome);
    }

    // Cadastra um novo usuário.
    @Operation(
            summary = "Cadastrar usuário",
            description = "Cadastra um novo usuário após validar os dados enviados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuário cadastrado com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Os dados enviados são inválidos."
            )
    })
    @PostMapping
    public ResponseEntity<Usuario> cadastrar(
            @Valid @RequestBody Usuario usuario) {

        return ResponseEntity
                .status(201)
                .body(service.salvar(usuario));
    }

    // Atualiza um usuário existente.

    @Operation(
            summary = "Atualizar usuário",
            description = "Atualiza os dados de um usuário existente pelo ID informado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Os dados enviados são inválidos."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Usuario usuario) {

        return service.atualizar(id, usuario)
                .map(usuarioAtualizado -> ResponseEntity.ok(usuarioAtualizado))
                .orElse(ResponseEntity.notFound().build());
    }

    // Exclui um usuário pelo ID.
    @Operation(
            summary = "Excluir usuário",
            description = "Exclui um usuário cadastrado pelo seu ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        if (service.excluir(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}