package br.com.autofacil.api.services;

import br.com.autofacil.api.dtos.user.UserRequestDTO;
import br.com.autofacil.api.dtos.user.UserResponseDTO;
import br.com.autofacil.api.dtos.user.UserUpdateDTO;
import br.com.autofacil.api.models.User;
import br.com.autofacil.api.repositories.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public UserResponseDTO createUser(UserRequestDTO dto) {
        User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPasswordHash(passwordEncoder.encode(dto.password()));
        user.setRole(dto.role());

        userRepo.save(user);
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    public List<UserResponseDTO> findAll() {
        return userRepo.findAll().stream()
                .map(u -> new UserResponseDTO(u.getId(), u.getName(), u.getEmail(), u.getRole()))
                .toList();
    }

    public UserResponseDTO findById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    public Optional<User> findByEmail(String email){
        return userRepo.findByEmail(email);
    }

    public Optional<User> findByUsername(String username){
        return userRepo.findByUsername(username);
    }

    public UserResponseDTO updateUser(Long id, UserUpdateDTO dto) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        user.setName(dto.name());
        user.setEmail(dto.email());
        userRepo.save(user);

        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }
}
