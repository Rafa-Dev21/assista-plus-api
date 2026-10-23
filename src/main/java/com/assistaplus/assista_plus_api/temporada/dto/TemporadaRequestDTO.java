package com.assistaplus.assista_plus_api.temporada.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Modelo de dados para cadastrar ou atualizar uma temporada")
public record TemporadaRequestDTO(
        @Schema(example = "1", description = "Número da temporada")
        @NotNull(message = "O número da temporada é obrigatório")
        @Min(value = 1, message = "O número da temporada deve ser maior ou igual a 1")
        Integer numero,

        @Schema(example = "1", description = "ID da série correspondente a esta temporada")
        @NotNull(message = "O ID da série é obrigatório")
        Long serieId
) {}
