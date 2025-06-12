package br.com.autofacil.api.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

public record CredentialsDTO(
        @Schema(description = "Email do usuário para autenticação", example = "comprador@example.com")
        String email,

        @Schema(description = "Senha do usuário para autenticação", example = "senha123")
        String password
) {}