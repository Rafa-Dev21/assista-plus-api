package com.assistaplus.assista_plus_api.serie.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Schema(description = "Modelo de dados para cadastrar ou atualizar uma série")
public record SerieRequestDTO(
        @Schema(example = "Stranger Things", description = "Título da série")
        @NotBlank(message = "O título é obrigatório")
        @Size(min = 2, max = 150)
        String titulo,

        @Schema(example = "Uma série de ficção científica e mistério.", description = "Sinopse ou descrição curta")
        @NotBlank(message = "A descrição é obrigatória")
        @Size(min = 10, max = 500)
        String descricao,

        @Schema(example = "2016", description = "Ano de lançamento oficial")
        @NotNull(message = "O ano de lançamento é obrigatório")
        @Min(1900) @Max(2100)
        Integer anoLancamento,

        @Schema(example = "https://m.media-amazon.com/images/M/MV5BYmQ4Yzg3NjAtMDM2My00MzgwLThjMTAtMmJiN2R3NWZhNVkxXkEyXkFqcGdeQXVyMTkxNjUyNQ@@._V1_.jpg", description = "URL da imagem ou pôster da série")
        String imagemUrl,

        @Schema(example = "EM_EXIBICAO", description = "Status atual da série")
        @NotNull(message = "O status é obrigatório")
        String status,

        @Schema(example = "1", description = "ID do diretor responsável")
        @NotNull(message = "O ID do diretor é obrigatório")
        Long diretorId,

        @Schema(example = "[1]", description = "Lista com os IDs dos gêneros correspondentes")
        List<Long> generosIds
) {}