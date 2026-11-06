package com.assistaplus.assista_plus_api.diretor.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de retorno de um diretor")
public record DiretorResponseDTO(
        @Schema(example = "1") Long id,
        @Schema(example = "Matt Duffer") String nome,
        @Schema(example = "Americano") String nacionalidade
) {}
