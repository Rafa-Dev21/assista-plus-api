package com.assistaplus.assista_plus_api.diretor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Modelo de dados para cadastrar ou atualizar um diretor")
public record DiretorRequestDTO(
        @Schema(example = "Matt Duffer", description = "Nome completo do diretor")
        @NotBlank(message = "O nome do diretor é obrigatório")
        @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
        String nome,

        @Schema(example = "Americano", description = "Nacionalidade do diretor")
        @NotBlank(message = "A nacionalidade é obrigatória")
        @Size(min = 2, max = 50, message = "A nacionalidade deve ter entre 2 e 50 caracteres")
        String nacionalidade
) {}
