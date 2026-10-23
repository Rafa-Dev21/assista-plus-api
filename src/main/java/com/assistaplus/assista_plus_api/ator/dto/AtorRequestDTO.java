package com.assistaplus.assista_plus_api.ator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Modelo de dados para cadastrar ou atualizar um ator")
public record AtorRequestDTO(
        @Schema(example = "Millie Bobby Brown", description = "Nome completo do ator")
        @NotBlank(message = "O nome do ator é obrigatório")
        @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
        String nome,

        @Schema(example = "Britânica", description = "Nacionalidade do ator")
        @NotBlank(message = "A nacionalidade é obrigatória")
        @Size(min = 2, max = 50, message = "A nacionalidade deve ter entre 2 e 50 caracteres")
        String nacionalidade
) {}
