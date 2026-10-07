
package com.assistaplus.assista_plus_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> tratarValidacao(
            MethodArgumentNotValidException ex) {

        Map<String, String> erros = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(erro ->
                erros.put(erro.getField(), erro.getDefaultMessage())
        );

        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("status", 400);
        resposta.put("erro", "Erro de validação");
        resposta.put("mensagens", erros);
        resposta.put("dataHora", LocalDateTime.now());

        return ResponseEntity.badRequest().body(resposta);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> tratarCorpoInvalido(
            HttpMessageNotReadableException ex) {

        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("status", 400);
        resposta.put("erro", "Corpo da requisição inválido");
        resposta.put("mensagem",
                "Verifique o JSON enviado e os tipos dos campos.");
        resposta.put("dataHora", LocalDateTime.now());

        return ResponseEntity.badRequest().body(resposta);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> tratarErroInterno(
            Exception ex) {

        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("status", 500);
        resposta.put("erro", "Erro interno do servidor");
        resposta.put("mensagem",
                "Não foi possível processar a requisição.");
        resposta.put("dataHora", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(resposta);
    }
}