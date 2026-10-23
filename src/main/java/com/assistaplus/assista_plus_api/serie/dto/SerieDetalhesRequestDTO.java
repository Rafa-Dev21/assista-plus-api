package com.assistaplus.assista_plus_api.serie.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Modelo de dados para cadastrar ou atualizar os detalhes de uma série")
public record SerieDetalhesRequestDTO(
        @Schema(example = "Inglês", description = "Idioma original da produção")
        @NotBlank(message = "O idioma original é obrigatório")
        @Size(min = 2, max = 50, message = "O idioma deve ter entre 2 e 50 caracteres")
        String idiomaOriginal,

        @Schema(example = "Estados Unidos", description = "País onde a série foi produzida")
        @NotBlank(message = "O país de origem é obrigatório")
        @Size(min = 2, max = 100, message = "O país deve ter entre 2 e 100 caracteres")
        String paisOrigem,

        @Schema(example = "16", description = "Classificação indicativa por idade (0 a 18 anos)")
        @NotNull(message = "A classificação indicativa é obrigatória")
        @Min(value = 0, message = "A classificação mínima é 0")
        @Max(value = 18, message = "A classificação máxima é 18")
        Integer classificacaoIndicativa,

        @Schema(example = "1", description = "ID da série correspondente a estes detalhes")
        @NotNull(message = "O ID da série é obrigatório")
        Long serieId
) {}
