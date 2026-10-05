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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    // Lista todos os usuários com paginação e HATEOAS.
    @GetMapping
    public PagedModel<EntityModel<Usuario>> listar(
            Pageable pageable,
            PagedResourcesAssembler<Usuario> assembler) {

        Page<Usuario> pagina = service.listar(pageable);

        return assembler.toModel(pagina);
    }

    // Busca um usuário pelo ID.
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
    @GetMapping("/buscar")
    public List<Usuario> buscarPorNome(@RequestParam String nome) {
        return service.buscarPorNome(nome);
    }

    // Cadastra um novo usuário.
    @PostMapping
    public ResponseEntity<Usuario> cadastrar(
            @Valid @RequestBody Usuario usuario) {

        return ResponseEntity
                .status(201)
                .body(service.salvar(usuario));
    }

    // Atualiza um usuário existente.
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Usuario usuario) {

        return service.atualizar(id, usuario)
                .map(usuarioAtualizado -> ResponseEntity.ok(usuarioAtualizado))
                .orElse(ResponseEntity.notFound().build());
    }

    // Exclui um usuário pelo ID.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        if (service.excluir(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}