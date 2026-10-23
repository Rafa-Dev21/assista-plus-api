package com.assistaplus.assista_plus_api.genero.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de retorno de um gênero")
public record GeneroResponseDTO(
        @Schema(example = "1") Long id,
        @Schema(example = "Terror") String nome
) {}
