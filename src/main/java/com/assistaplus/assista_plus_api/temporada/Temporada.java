package com.assistaplus.assista_plus_api.temporada;

import com.assistaplus.assista_plus_api.serie.Serie;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


@Entity
public class Temporada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Min(1)
    private Integer numero;

    //várias temporadas podem pertencer à mesma série.
    //Por isso, no lado da temporada usamos @ManyToOne, criando uma relação muitos-para-um com a entidade Serie.

    @NotNull
    @ManyToOne
    @JoinColumn(name = "serie_id")
    private Serie serie;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Serie getSerie() {
        return serie;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }
}