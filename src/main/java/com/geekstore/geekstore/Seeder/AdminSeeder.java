package com.geekstore.geekstore.Seeder;

import com.geekstore.geekstore.Model.User;
import com.geekstore.geekstore.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Verifica se já existe um admin
        if(userRepository.findByRole("ADMIN").isEmpty()) {
            User admin = new User();
            admin.setName("Administrador");
            admin.setEmail("admin@geekstore.com");
            admin.setPassword(passwordEncoder.encode("admin123")); // criptografa a senha
            admin.setRole("ADMIN");

            userRepository.save(admin);
            System.out.println("Admin criado com sucesso!");
        } else {
            System.out.println("Admin já existe, nenhum seeder foi executado.");
        }
    }
}
