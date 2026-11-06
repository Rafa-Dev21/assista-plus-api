package com.assistaplus.assista_plus_api.episodio.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de retorno de um episódio")
public record EpisodioResponseDTO(
        @Schema(example = "1") Long id,
        @Schema(example = "1") Integer numero,
        @Schema(example = "Capítulo Um: O Desaparecimento de Will Byers") String titulo,
        @Schema(example = "48") Integer duracao,
        @Schema(example = "1") Long temporadaId,
        @Schema(example = "1") Integer temporadaNumero
) {}
