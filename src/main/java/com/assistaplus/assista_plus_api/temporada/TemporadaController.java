package com.assistaplus.assista_plus_api.temporada;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// @RestController informa ao Spring que esta classe
// será responsável por receber e responder requisições HTTP.

// O Controller é a camada responsável por receber as requisições
// e encaminhá-las para o Service.

@RestController
@RequestMapping("/temporadas")
public class TemporadaController {

    @Autowired
    private TemporadaService service;

    // O Controller recebe a requisição GET e envia o Pageable
    // para o Service, permitindo listar as temporadas com paginação.

    @GetMapping
    public Page<Temporada> listar(Pageable pageable) {
        return service.listar(pageable);
    }

    // Utilizamos @PathVariable para receber o ID informado na URL.
    // ResponseEntity permite retornar 200 quando a temporada existe
    // e 404 quando o ID não foi encontrado.

    @GetMapping("/{id}")
    public ResponseEntity<Temporada> buscarPorId(@PathVariable Long id) {

        return service.buscarPorId(id)
                .map(temporada -> ResponseEntity.ok(temporada))
                .orElse(ResponseEntity.notFound().build());
    }

    // @PostMapping é utilizado para cadastrar uma nova temporada.
    // @Valid ativa as validações definidas na entidade.

    @PostMapping
    public Temporada cadastrar(@Valid @RequestBody Temporada temporada) {
        return service.salvar(temporada);
    }

    // Utilizamos PUT para atualizar uma temporada existente.
    // O ID vem pela URL e os novos dados são enviados no corpo.

    @PutMapping("/{id}")
    public ResponseEntity<Temporada> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Temporada temporada) {

        return service.atualizar(id, temporada)
                .map(temporadaAtualizada -> ResponseEntity.ok(temporadaAtualizada))
                .orElse(ResponseEntity.notFound().build());
    }

    // Quando a exclusão acontece, retornamos 204 No Content.
    // Caso a temporada não exista, retornamos 404 Not Found.

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        if (service.excluir(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}