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
    //para o Service, que busca as séries de forma paginada."

    @GetMapping
    public Page<Serie> listar(Pageable pageable) {
        return service.listar(pageable);
    }

    @PostMapping
    public Serie cadastrar(@Valid @RequestBody Serie serie) {
        return service.salvar(serie);
    }




}