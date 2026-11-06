package com.assistaplus.assista_plus_api.idempotencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_idempotencia")
public class IdempotenciaRegistro {

    @Id
    @Column(name = "chave_idempotencia", unique = true, nullable = false)
    private String chave;

    private Integer statusHttp;

    @Column(columnDefinition = "TEXT")
    private String respostaJson;

    @Column(nullable = false)
    private String requestHash; // Hash SHA-256 do corpo da requisição para validação de payload (422)

    private LocalDateTime dataCriacao = LocalDateTime.now();

    // Getters e Setters
    public String getChave() { return chave; }
    public void setChave(String chave) { this.chave = chave; }

    public Integer getStatusHttp() { return statusHttp; }
    public void setStatusHttp(Integer statusHttp) { this.statusHttp = statusHttp; }

    public String getRespostaJson() { return respostaJson; }
    public void setRespostaJson(String respostaJson) { this.respostaJson = respostaJson; }

    public String getRequestHash() { return requestHash; }
    public void setRequestHash(String requestHash) { this.requestHash = requestHash; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}