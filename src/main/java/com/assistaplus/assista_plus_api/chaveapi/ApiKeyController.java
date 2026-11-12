package com.assistaplus.assista_plus_api.chaveapi;

import com.assistaplus.assista_plus_api.model.ApiKey;
import com.assistaplus.assista_plus_api.repository.ApiKeyRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/keys")
@Tag(name = "Chaves de API", description = "Gerenciamento e geração de chaves de autenticação para consumo da API")
public class ApiKeyController {

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @PostMapping
    @Operation(
            summary = "Gerar nova chave de API",
            description = "Cria uma nova chave de acesso ativa associada a um proprietário. " +
                    "Caso já exista uma chave cadastrada para o mesmo proprietário, a operação é rejeitada com status 409 Conflict."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Chave de API criada com sucesso."),
            @ApiResponse(responseCode = "409", description = "Conflito: Já existe uma chave cadastrada para este proprietário."),
            @ApiResponse(responseCode = "400", description = "Erro interno ao processar a requisição de cadastro."),
            @ApiResponse(responseCode = "429", description = "Muitas requisições: Limite de taxa (Rate Limit) excedido para o IP.")
    })
    public ResponseEntity<Object> gerarChave(
            @Parameter(description = "Nome do proprietário da chave de API", required = true, example = "Rafael")
            @RequestParam String ownerName) {

        // Verifica se já existe uma chave cadastrada para esse ownerName
        Optional<ApiKey> existingKey = apiKeyRepository.findAll().stream()
                .filter(k -> k.getOwnerName().equalsIgnoreCase(ownerName))
                .findFirst();

        if (existingKey.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Já existe uma chave de API cadastrada para o proprietário: " + ownerName);
        }

        try {
            ApiKey novaChave = new ApiKey();
            novaChave.setKeyValue("key-" + UUID.randomUUID().toString());
            novaChave.setOwnerName(ownerName);
            novaChave.setRole("USER"); // Garante o padrão no banco sem exigir parâmetro externo
            novaChave.setActive(true);
            novaChave.setCreatedAt(LocalDateTime.now());

            ApiKey salva = apiKeyRepository.save(novaChave);
            return ResponseEntity.status(HttpStatus.CREATED).body(salva);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao gerar chave de API: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(
            summary = "Listar chaves de API",
            description = "Retorna a listagem completa de todas as chaves de acesso cadastradas no sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listagem retornada com sucesso."),
            @ApiResponse(responseCode = "429", description = "Muitas requisições: Limite de taxa (Rate Limit) excedido para o IP.")
    })
    public ResponseEntity<List<ApiKey>> listarChaves() {
        return ResponseEntity.ok(apiKeyRepository.findAll());
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Remover chave de API",
            description = "Exclui permanentemente uma chave de API cadastrada com base no seu ID numérico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Chave de API removida com sucesso."),
            @ApiResponse(responseCode = "404", description = "Chave de API não encontrada para o ID informado."),
            @ApiResponse(responseCode = "429", description = "Muitas requisições: Limite de taxa (Rate Limit) excedido para o IP.")
    })
    public ResponseEntity<Void> deletarChave(
            @Parameter(description = "ID da chave de API a ser deletada", required = true, example = "1")
            @PathVariable Long id) {

        if (!apiKeyRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        apiKeyRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}