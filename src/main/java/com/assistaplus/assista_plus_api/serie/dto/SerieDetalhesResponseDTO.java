package com.assistaplus.assista_plus_api.serie.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de retorno das especificações técnicas de uma série")
public record SerieDetalhesResponseDTO(
        @Schema(example = "1") Long id,
        @Schema(example = "Inglês") String idiomaOriginal,
        @Schema(example = "Estados Unidos") String paisOrigem,
        @Schema(example = "16") Integer classificacaoIndicativa,
        @Schema(example = "1") Long serieId,
        @Schema(example = "Stranger Things") String serieTitulo
) {}
