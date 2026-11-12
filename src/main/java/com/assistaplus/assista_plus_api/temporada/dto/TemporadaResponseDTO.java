package com.assistaplus.assista_plus_api.temporada.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de retorno de uma temporada")
public record TemporadaResponseDTO(
        @Schema(example = "1") Long id,
        @Schema(example = "1") Integer numero,
        @Schema(example = "1") Long serieId,
        @Schema(example = "Stranger Things") String serieTitulo
) {}
