package com.assistaplus.assista_plus_api.avaliacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Modelo de dados para registrar ou atualizar uma avaliação")
public record AvaliacaoRequestDTO(
        @Schema(example = "5", description = "Nota atribuída à série (de 1 a 5)")
        @NotNull(message = "A nota é obrigatória")
        @Min(value = 1, message = "A nota mínima é 1")
        @Max(value = 5, message = "A nota máxima é 5")
        Integer nota,

        @Schema(example = "Uma das melhores produções originais da plataforma!", description = "Comentário ou crítica sobre a série")
        @Size(max = 500, message = "O comentário não pode ultrapassar 500 caracteres")
        String comentario,

        @Schema(example = "1", description = "ID do usuário que está avaliando")
        @NotNull(message = "O ID do usuário é obrigatório")
        Long usuarioId,

        @Schema(example = "1", description = "ID da série avaliada")
        @NotNull(message = "O ID da série é obrigatório")
        Long serieId
) {}
