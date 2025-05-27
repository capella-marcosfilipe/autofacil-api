package br.com.autofacil.api.dtos.user;

import br.com.autofacil.api.models.UserRole;

public record UserRequestDTO (
    String name,
    String email,
    String password,
    UserRole role
) {
}
