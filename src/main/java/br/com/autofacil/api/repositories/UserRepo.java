package br.com.autofacil.api.repositories;

import br.com.autofacil.api.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
}
