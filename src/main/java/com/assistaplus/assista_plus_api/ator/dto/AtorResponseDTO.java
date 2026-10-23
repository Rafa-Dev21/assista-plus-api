package com.assistaplus.assista_plus_api.ator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de retorno de um ator")
public record AtorResponseDTO(
        @Schema(example = "1") Long id,
        @Schema(example = "Millie Bobby Brown") String nome,
        @Schema(example = "Britânica") String nacionalidade
) {}
