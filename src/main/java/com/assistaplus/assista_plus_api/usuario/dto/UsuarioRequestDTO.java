package com.assistaplus.assista_plus_api.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Modelo de dados para cadastrar ou atualizar um usuário")
public record UsuarioRequestDTO(
        @Schema(example = "Rafael de Oliveira", description = "Nome completo do usuário")
        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
        String nome,

        @Schema(example = "rafael@example.com", description = "E-mail único de acesso à plataforma")
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "O formato do e-mail é inválido")
        @Size(min = 5, max = 150, message = "O e-mail deve ter entre 5 e 150 caracteres")
        String email,

        @Schema(example = "senha123", description = "Senha de acesso do usuário")
        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 6, max = 50, message = "A senha deve ter entre 6 e 50 caracteres")
        String senha
) {}