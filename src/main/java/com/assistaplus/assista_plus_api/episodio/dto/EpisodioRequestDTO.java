package com.assistaplus.assista_plus_api.episodio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Modelo de dados para cadastrar ou atualizar um episódio")
public record EpisodioRequestDTO(
        @Schema(example = "1", description = "Número do episódio dentro da temporada")
        @NotNull(message = "O número do episódio é obrigatório")
        @Min(value = 1, message = "O número deve ser maior ou igual a 1")
        Integer numero,

        @Schema(example = "Capítulo Um: O Desaparecimento de Will Byers", description = "Título do episódio")
        @NotBlank(message = "O título é obrigatório")
        @Size(min = 2, max = 150, message = "O título deve ter entre 2 e 150 caracteres")
        String titulo,

        @Schema(example = "48", description = "Duração do episódio em minutos")
        @NotNull(message = "A duração é obrigatória")
        @Min(value = 1, message = "A duração deve ser de pelo menos 1 minuto")
        Integer duracao,

        @Schema(example = "1", description = "ID da temporada correspondente")
        @NotNull(message = "O ID da temporada é obrigatório")
        Long temporadaId
) {}
