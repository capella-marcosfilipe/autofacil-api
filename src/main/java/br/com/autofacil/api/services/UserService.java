package br.com.autofacil.api.services;

import br.com.autofacil.api.models.User;
import br.com.autofacil.api.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired private UserRepo userRepo;

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public Optional<User> getUserById(Long id){
        return userRepo.findById(id);
    }

    public Optional<User> getUserByEmail(String email){
        return userRepo.findByEmail(email);
    }

    public Optional<User> getUserByUsername(String username){
        return userRepo.findByUsername(username);
    }

    public User createUser(User user) {
        user.setCreatedAt(java.time.LocalDateTime.now());
        user.setUpdatedAt(java.time.LocalDateTime.now());
        return userRepo.save(user);
    }

    public User updateUser(User user) {
        user.setUpdatedAt(java.time.LocalDateTime.now());
        return userRepo.save(user);
    }

    public void deleteUser(Long id){
        userRepo.deleteById(id);
    }
}
