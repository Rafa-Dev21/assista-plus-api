package com.assistaplus.assista_plus_api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;

// O JsonInclude impede que o campo 'errosValidação' apareça no JSON se ele for nulo (ex: num erro 404)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DetalhesErro(
        int status,
        String erro,
        String mensagem,
        Map<String, String> errosValidacao,
        LocalDateTime dataHora
) {}
