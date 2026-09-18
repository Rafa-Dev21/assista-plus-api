package com.assistaplus.assista_plus_api.serie;

// Importa a anotação que transforma a classe em uma entidade JPA.
import jakarta.persistence.Entity;
// Importa a anotação responsável pela geração automática do ID.
import jakarta.persistence.GeneratedValue;
// Importa as estratégias disponíveis para geração do ID.
import jakarta.persistence.GenerationType;
// Importa a anotação que define a chave primária da tabela.
import jakarta.persistence.Id;
// Importa a anotação usada para definir como o enum será salvo no banco.
import jakarta.persistence.EnumType;
// Importa a anotação que configura o armazenamento do enum.
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;


// @Entity informa ao JPA/Hibernate que esta classe representa
// uma entidade que será mapeada para uma tabela no banco de dados.
@Entity
public class Serie {

    //@Id define o campo id como a chave primária da tabela.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Armazena o título ou nome da série.
    //@NotBlank garante que o título seja preenchido.
    @NotBlank
    private String titulo;

    //Armazena a descrição ou sinopse da série.
    //Usei @NotBlank para garantir que a série tenha descricao preenchida.
    @NotBlank
    private String descricao;

    // Armazena o ano de lançamento da série.
    //Usei @NotNull porque o ano de lançamento é um campo numérico
    //Min e Max foi implementado para validar o ano da serie
    @NotNull
    @Min(1900)
    @Max(2100)
    private Integer anoLancamento;

    //Usei @Enumerated com EnumType.STRING para armazenar
    @NotNull
    @Enumerated(EnumType.STRING)
    private StatusSerie status;

    //Getter do ID.
//Permite que outras classes consultem o ID da série.
//O getter permite acessar o valor privado do atributo.
    public Long getId() {
        return id;
    }

    // Setter do ID.
//Permite alterar o ID quando necessário.
    public void setId(Long id) {
        this.id = id;
    }

    //Getter do título.
//Retorna o título da série.
    public String getTitulo() {
        return titulo;
    }

    //Setter do título.
//Permite alterar o título da série.
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    //Getter da descrição.
//Retorna a descrição da série.
    public String getDescricao() {
        return descricao;
    }

    //Setter da descrição.
//Permite alterar a descrição da série.
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    //Getter do ano de lançamento.
//Retorna o ano de lançamento da série.
    public Integer getAnoLancamento() {
        return anoLancamento;
    }

    //Setter do ano de lançamento.
//Permite alterar o ano de lançamento da série.
    public void setAnoLancamento(Integer anoLancamento) {
        this.anoLancamento = anoLancamento;
    }

    //Getter do status.
//Retorna o status atual da série.
    public StatusSerie getStatus() {
        return status;
    }

    //Setter do status.
//Permite alterar o status da série.
    public void setStatus(StatusSerie status) {
        this.status = status;
    }


}



