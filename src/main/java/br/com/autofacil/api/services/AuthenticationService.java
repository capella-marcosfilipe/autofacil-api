package br.com.autofacil.api.services;

import br.com.autofacil.api.models.User;
import br.com.autofacil.api.models.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Autentica um usuário com base no email e senha e verifica seu papel (ROLE).
     *
     * @param email O email do usuário.
     * @param password A senha do usuário.
     * @param expectedRole O papel esperado para o usuário (ex: VENDOR ou BUYER).
     * @return O objeto User se a autenticação e verificação de papel forem bem-sucedidas.
     * @throws SecurityException Se as credenciais estiverem incorretas ou o usuário não tiver o papel esperado.
     */
    public User authenticateAndVerifyRole(String email, String password, UserRole expectedRole) {
        // Busca o usuário pelo email
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new SecurityException("Autenticação falhou: Email ou senha incorretos."));

        // Verifica a senha
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new SecurityException("Autenticação falhou: Email ou senha incorretos.");
        }

        // Verifica se o usuário tem o papel esperado
        if (user.getRole() != expectedRole) {
            throw new SecurityException("Acesso negado: O usuário não tem a permissão necessária (" + expectedRole + ").");
        }

        return user;
    }
}
