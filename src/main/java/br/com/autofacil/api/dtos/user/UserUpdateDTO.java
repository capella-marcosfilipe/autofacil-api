package br.com.autofacil.api.dtos.user;

public record UserUpdateDTO(
        String name,
        String email,
        String phonenumber,
        String cpf
) {
}
