// Arquivo: src/main/java/br/com/autofacil/api/config/SecurityConfig.java

package br.com.autofacil.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Este método declara o BCryptPasswordEncoder como um Bean gerenciado pelo Spring.
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Este método configura as regras de segurança HTTP da aplicação.
     *
     * @param http O objeto HttpSecurity para configurar a segurança.
     * @return O filtro de segurança construído.
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desabilita o CSRF (Cross-Site Request Forgery), pois não usamos sessões/cookies para autenticação.
                .csrf(AbstractHttpConfigurer::disable)

                // Configura a gestão de sessão para ser STATELESS, pois usamos uma API REST.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configura as regras de autorização para as requisições HTTP.
                .authorizeHttpRequests(authorize -> authorize
                                // Permite o acesso a TODOS os endpoints ("/**") sem autenticação.
                                .requestMatchers("/**").permitAll()
                        // Você poderia configurar rotas específicas aqui no futuro, se necessário.
                        // .requestMatchers("/admin/**").hasRole("ADMIN")
                        // .anyRequest().authenticated()
                );

        return http.build();
    }
}