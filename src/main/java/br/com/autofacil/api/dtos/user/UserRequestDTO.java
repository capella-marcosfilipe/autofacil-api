package br.com.autofacil.api.dtos.user;

import br.com.autofacil.api.models.UserRole;

import java.time.LocalDate;

public record UserRequestDTO (
    String name,
    String email,
    String password,
    UserRole role,
    String phonenumber,
    String cpf,
    LocalDate dateOfBirth
) {
}
