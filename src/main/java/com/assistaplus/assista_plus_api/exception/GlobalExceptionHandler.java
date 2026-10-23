package com.assistaplus.assista_plus_api.exception;

import jakarta.persistence.EntityNotFoundException;
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

    // 1. Trata erros de validação (Campos em branco, tamanho incorreto, etc.) -> Retorna 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DetalhesErro> tratarValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(erro ->
                erros.put(erro.getField(), erro.getDefaultMessage())
        );

        DetalhesErro resposta = new DetalhesErro(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validação",
                "Um ou mais campos foram enviados incorretamente.",
                erros,
                LocalDateTime.now()
        );

        return ResponseEntity.badRequest().body(resposta);
    }

    // 2. Trata JSON malformado ou tipos de dados inválidos -> Retorna 400
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DetalhesErro> tratarCorpoInvalido(HttpMessageNotReadableException ex) {
        DetalhesErro resposta = new DetalhesErro(
                HttpStatus.BAD_REQUEST.value(),
                "Corpo da requisição inválido",
                "Verifique o JSON enviado e os tipos dos campos.",
                null,
                LocalDateTime.now()
        );

        return ResponseEntity.badRequest().body(resposta);
    }

    // 🔥 3. Trata quando um recurso não é encontrado no banco pelo ID -> Retorna 404
    @ExceptionHandler({EntityNotFoundException.class, IllegalArgumentException.class})
    public ResponseEntity<DetalhesErro> tratarRecursoNaoEncontrado(Exception ex) {
        DetalhesErro resposta = new DetalhesErro(
                HttpStatus.NOT_FOUND.value(),
                "Recurso não encontrado",
                ex.getMessage() != null ? ex.getMessage() : "O identificador informado não corresponde a nenhum registro.",
                null,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta);
    }

    // 🔥 4. Trata exceções manuais lançadas com ResponseStatusException (como o 409 do Service) -> Retorna o status correto (ex: 409)
    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public ResponseEntity<DetalhesErro> tratarResponseStatus(org.springframework.web.server.ResponseStatusException ex) {
        DetalhesErro resposta = new DetalhesErro(
                ex.getStatusCode().value(),
                ex.getStatusCode().value() == 409 ? "Conflito de dados" : "Erro na requisição",
                ex.getReason() != null ? ex.getReason() : ex.getMessage(),
                null,
                LocalDateTime.now()
        );

        return ResponseEntity.status(ex.getStatusCode()).body(resposta);
    }

    // 5. Trata quando o usuário digita texto na URL onde deveria ser um número (ex: /diretores/ss) -> Retorna 400
    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public ResponseEntity<DetalhesErro> tratarErroTipoParametro(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex) {
        String mensagem = String.format("O parâmetro '%s' recebeu o valor '%s' que é inválido. Esperava-se um valor do tipo %s.",
                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());

        DetalhesErro resposta = new DetalhesErro(
                HttpStatus.BAD_REQUEST.value(),
                "Parâmetro de URL inválido",
                mensagem,
                null,
                LocalDateTime.now()
        );

        return ResponseEntity.badRequest().body(resposta);
    }

    // 🔥 6. Trata erros de violação de integridade no banco (como e-mail duplicado no unique = true) -> Retorna 409 Conflict
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<DetalhesErro> tratarErroIntegridadeBanco(org.springframework.dao.DataIntegrityViolationException ex) {
        DetalhesErro resposta = new DetalhesErro(
                HttpStatus.CONFLICT.value(),
                "Conflito de dados",
                "O registro informado (como o e-mail) já existe ou viola uma restrição de unicidade no sistema.",
                null,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(resposta);
    }

    // 7. Fallback para qualquer outro erro interno inesperado do sistema -> Retorna 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<DetalhesErro> tratarErroInterno(Exception ex) {
        DetalhesErro resposta = new DetalhesErro(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno do servidor",
                "Não foi possível processar a requisição devido a uma falha interna.",
                null,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resposta);
    }
}