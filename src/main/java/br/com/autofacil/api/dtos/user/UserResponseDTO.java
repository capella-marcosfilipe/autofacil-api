package br.com.autofacil.api.dtos.user;

import br.com.autofacil.api.models.UserRole;

public record UserResponseDTO(
        Long id,
        String name,
        String email,
        UserRole role
) {
}
