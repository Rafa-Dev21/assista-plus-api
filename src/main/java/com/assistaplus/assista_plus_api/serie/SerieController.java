package com.assistaplus.assista_plus_api.serie;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import com.assistaplus.assista_plus_api.diretor.DiretorController;
import org.springframework.hateoas.PagedModel;
import org.springframework.data.web.PagedResourcesAssembler;

import java.util.List;

// @RestController informa ao Spring que esta classe será responsável
// por receber e responder requisições HTTP.


//O Controller é a camada responsável por receber as requisições
//HTTP e encaminhá-las para o Service.

@RequestMapping("/series")
@RestController
public class SerieController {

    @Autowired
    private SerieService service;

    //O Controller recebe a requisição GET e envia o Pageable
    //para o Service, que busca as séries de forma paginada.

    @GetMapping
    public PagedModel<EntityModel<Serie>> listar(
            Pageable pageable,
            PagedResourcesAssembler<Serie> assembler) {

        Page<Serie> pagina = service.listar(pageable);

        return assembler.toModel(pagina);
    }

    // Busca séries pelo título informado na URL.
    @GetMapping("/buscar")
    public List<Serie> buscarPorTitulo(@RequestParam String titulo) {
        return service.buscarPorTitulo(titulo);
    }

    //Utilizei @PathVariable para receber o ID informado na URL e buscar a série correspondente no banco
    //usei o ResponseEntity para retornar 200 quando a série existe e 404 quando o ID não foi encontrado
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Serie>> buscarPorId(@PathVariable Long id) {

        return service.buscarPorId(id)
                .map(serie -> {

                    EntityModel<Serie> model = EntityModel.of(serie);

                    model.add(linkTo(methodOn(SerieController.class)
                            .buscarPorId(id))
                            .withSelfRel());

                    model.add(linkTo(methodOn(SerieController.class)
                            .atualizar(id, serie))
                            .withRel("atualizar"));

                    model.add(linkTo(methodOn(SerieController.class)
                            .excluir(id))
                            .withRel("excluir"));

                    if (serie.getDiretor() != null) {
                        model.add(linkTo(methodOn(DiretorController.class)
                                .buscarPorId(serie.getDiretor().getId()))
                                .withRel("diretor"));
                    }

                    return ResponseEntity.ok(model);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    public Serie cadastrar(@Valid @RequestBody Serie serie) {
        return service.salvar(serie);
    }

    //Utilizei o PUT para atualizar uma série existente. O ID vem pela URL e os novos dados são enviados no corpo
    //da requisição
    @PutMapping("/{id}")
    public ResponseEntity<Serie> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Serie serie) {

        return service.atualizar(id, serie)
                .map(serieAtualizada -> ResponseEntity.ok(serieAtualizada))
                .orElse(ResponseEntity.notFound().build());
    }

    // Quando a exclusão acontece, retorno 204 No Content. Caso a série não exista, retorno 404 Not Found
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        if (service.excluir(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    //  Adiciona um gênero a uma série existente.
    //O ID da série e o ID do gênero são informados pela URL.
    @PatchMapping("/{serieId}/generos/{generoId}")
    public ResponseEntity<Serie> adicionarGenero(
            @PathVariable Long serieId,
            @PathVariable Long generoId) {

        return service.adicionarGenero(serieId, generoId)
                .map(serie -> ResponseEntity.ok(serie))
                .orElse(ResponseEntity.notFound().build());
    }



}