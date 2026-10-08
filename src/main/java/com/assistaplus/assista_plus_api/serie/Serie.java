package com.assistaplus.assista_plus_api.serie;

// Importa a entidade Ator para criar o relacionamento Many-to-Many.
import com.assistaplus.assista_plus_api.ator.Ator;

// Importa a entidade Diretor para criar o relacionamento Many-to-One.
import com.assistaplus.assista_plus_api.diretor.Diretor;

// Importa a anotação que evita loop infinito na conversão para JSON.
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.assistaplus.assista_plus_api.genero.Genero;

// Anotações do JPA.
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

// Anotações de validação.
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.JoinTable;
import jakarta.validation.constraints.Size;


// @Entity informa ao JPA/Hibernate que esta classe representa
// uma entidade que será mapeada para uma tabela no banco de dados.
@Entity
public class Serie {

    // @Id define o campo id como a chave primária da tabela.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Armazena o título ou nome da série.
    // @NotBlank garante que o título seja preenchido.
    @NotBlank
    @Size(min = 2, max = 150)
    private String titulo;

    // Armazena a descrição ou sinopse da série.
    // @NotBlank garante que a descrição seja preenchida.
    @NotBlank
    @Size(min = 10, max = 500)
    private String descricao;

    // Armazena o ano de lançamento da série.
    // @NotNull garante que o campo seja informado.
    // @Min e @Max validam o intervalo permitido para o ano.
    @NotNull
    @Min(1900)
    @Max(2100)
    private Integer anoLancamento;

    // Define o status atual da série.
    // @Enumerated(EnumType.STRING) salva o nome do enum no banco.
    @NotNull
    @Enumerated(EnumType.STRING)
    private StatusSerie status;

    // Uma série pode ter vários atores.
    // O mappedBy informa que o relacionamento é controlado pela entidade Ator.
    //
    // @JsonIgnore evita um loop infinito na resposta JSON:
    // Serie -> Ator -> Serie -> Ator...
    @JsonIgnore
    @ManyToMany(mappedBy = "series")
    private Set<Ator> atores = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "serie_genero",
            joinColumns = @JoinColumn(name = "serie_id"),
            inverseJoinColumns = @JoinColumn(name = "genero_id")
    )
    private Set<Genero> generos = new HashSet<>();


    // Várias séries podem estar relacionadas ao mesmo diretor.
    //
    // @ManyToOne representa o relacionamento:
    // muitas séries -> um diretor.
    //
    // @JoinColumn cria a coluna diretor_id na tabela serie.
    //
    // Não utilizamos @JsonIgnore aqui porque precisamos que
    // o Jackson consiga receber o diretor enviado no JSON.
    //
    // Exemplo:
    // "diretor": {
    //     "id": 1
    // }

    @ManyToOne
    @JoinColumn(name = "diretor_id")
    private Diretor diretor;


    // Getter do ID.
    // Permite consultar o ID da série.
    public Long getId() {
        return id;
    }

    // Setter do ID.
    // Permite alterar o ID da série.
    public void setId(Long id) {
        this.id = id;
    }


    // Getter do título.
    // Retorna o título da série.
    public String getTitulo() {
        return titulo;
    }

    // Setter do título.
    // Permite alterar o título da série.
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }


    // Getter da descrição.
    // Retorna a descrição da série.
    public String getDescricao() {
        return descricao;
    }

    // Setter da descrição.
    // Permite alterar a descrição da série.
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }


    // Getter do ano de lançamento.
    // Retorna o ano de lançamento da série.
    public Integer getAnoLancamento() {
        return anoLancamento;
    }

    // Setter do ano de lançamento.
    // Permite alterar o ano de lançamento da série.
    public void setAnoLancamento(Integer anoLancamento) {
        this.anoLancamento = anoLancamento;
    }


    // Getter do status.
    // Retorna o status atual da série.
    public StatusSerie getStatus() {
        return status;
    }

    // Setter do status.
    // Permite alterar o status da série.
    public void setStatus(StatusSerie status) {
        this.status = status;
    }


    // Getter dos atores.
    // Retorna os atores relacionados à série.
    public Set<Ator> getAtores() {
        return atores;
    }

    // Setter dos atores.
    // Permite alterar os atores relacionados à série.
    public void setAtores(Set<Ator> atores) {
        this.atores = atores;
    }


    // Getter do diretor.
    // Retorna o diretor relacionado à série.
    public Diretor getDiretor() {
        return diretor;
    }

    // Setter do diretor.
    // Permite alterar o diretor relacionado à série.
    public void setDiretor(Diretor diretor) {
        this.diretor = diretor;
    }

    public Set<Genero> getGeneros() {
        return generos;
    }

    public void setGeneros(Set<Genero> generos) {
        this.generos = generos;
    }
}
