package com.assistaplus.assista_plus_api.serie.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Dados simplificados de retorno de uma série")
public record SerieResponseDTO(
        @Schema(example = "1") Long id,
        @Schema(example = "Stranger Things") String titulo,
        @Schema(example = "Uma série de ficção científica e mistério.") String descricao,
        @Schema(example = "2016") Integer anoLancamento,
        @Schema(example = "https://m.media-amazon.com/images/M/MV5BYmQ4Yzg3NjAtMDM2My00MzgwLThjMTAtMmJiN2R3NWZhNVkxXkEyXkFqcGdeQXVyMTkxNjUyNQ@@._V1_.jpg")
        String imagemUrl,
        @Schema(example = "EM_EXIBICAO") String status,
        @Schema(example = "Matt Duffer") String nomeDiretor,
        List<String> nomeGeneros
) {}