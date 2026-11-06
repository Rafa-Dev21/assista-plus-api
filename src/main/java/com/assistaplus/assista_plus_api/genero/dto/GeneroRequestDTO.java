package com.assistaplus.assista_plus_api.genero.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Modelo de dados para cadastrar ou atualizar um gênero")
public record GeneroRequestDTO(
        @Schema(example = "Terror", description = "Nome do gênero cinematográfico")
        @NotBlank(message = "O nome do gênero é obrigatório")
        @Size(min = 2, max = 50, message = "O nome deve ter entre 2 e 50 caracteres")
        String nome
) {}
