package com.assistaplus.assista_plus_api.avaliacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de retorno de uma avaliação")
public record AvaliacaoResponseDTO(
        @Schema(example = "1") Long id,
        @Schema(example = "5") Integer nota,
        @Schema(example = "Uma das melhores produções originais da plataforma!") String comentario,
        @Schema(example = "1") Long usuarioId,
        @Schema(example = "Rafael de Oliveira") String usuarioNome,
        @Schema(example = "1") Long serieId,
        @Schema(example = "Stranger Things") String serieTitulo
) {}
