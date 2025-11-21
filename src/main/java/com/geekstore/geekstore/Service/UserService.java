package com.geekstore.geekstore.Service;

import com.geekstore.geekstore.Model.User;
import com.geekstore.geekstore.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    // Método adicional
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + email));
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    // Dentro do UserService
    public List<User> findByRole(String role) {
        return userRepository.findByRole(role);
    }
}