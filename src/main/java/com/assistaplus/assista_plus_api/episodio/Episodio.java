package com.assistaplus.assista_plus_api.episodio;

import com.assistaplus.assista_plus_api.temporada.Temporada;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Episodio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Min(1)
    private Integer numero;

    @NotBlank
    private String titulo;

    @NotNull
    @Min(1)
    private Integer duracao;

    // Vários episódios podem pertencer à mesma temporada.
    // Por isso, no lado do episódio usamos @ManyToOne,
    // criando uma relação muitos-para-um com a entidade Temporada.

    @NotNull
    @ManyToOne
    @JoinColumn(name = "temporada_id")
    private Temporada temporada;

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

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getDuracao() {
        return duracao;
    }

    public void setDuracao(Integer duracao) {
        this.duracao = duracao;
    }

    public Temporada getTemporada() {
        return temporada;
    }

    public void setTemporada(Temporada temporada) {
        this.temporada = temporada;
    }


}