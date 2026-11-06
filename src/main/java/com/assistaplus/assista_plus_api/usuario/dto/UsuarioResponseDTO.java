package com.assistaplus.assista_plus_api.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de retorno de um usuário")
public record UsuarioResponseDTO(
        @Schema(example = "1") Long id,
        @Schema(example = "Rafael de Oliveira") String nome,
        @Schema(example = "rafael@example.com") String email
) {}
