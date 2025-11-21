package com.geekstore.geekstore.Controller;

import com.geekstore.geekstore.Model.User;
import com.geekstore.geekstore.Service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AdminUserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // ===== LISTAGEM DE USUÁRIOS =====
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/user/index"; // Template: src/main/resources/templates/admin/user/index.html
    }

    // ===== CRIAR NOVO USUÁRIO =====
    @PostMapping
    public String saveUser(@ModelAttribute("user") User user, Model model) {
        try {
            // Criptografa senha antes de salvar
            if (user.getPassword() != null && !user.getPassword().isBlank()) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            userService.save(user);
            return "redirect:/admin/users";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Erro ao salvar usuário: " + e.getMessage());
            return "redirect:/admin/users";
        }
    }

    // ===== ATUALIZAR USUÁRIO (EDIÇÃO) =====
    @PostMapping("/edit/{id}")
    public String updateUser(
            @PathVariable Long id,
            @ModelAttribute("user") User updatedUser,
            Model model) {
        try {
            // 1. Busca o usuário existente
            User existingUser = userService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário inválido: " + id));

            // 2. Atualiza os campos básicos
            existingUser.setName(updatedUser.getName());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setRole(updatedUser.getRole());

            // 3. Atualiza senha se foi informada
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
                existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

            // 4. Salva novamente
            userService.save(existingUser);
            return "redirect:/admin/users";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Erro ao atualizar usuário: " + e.getMessage());
            return "redirect:/admin/users";
        }
    }

    // ===== DELETAR USUÁRIO =====
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/admin/users";
    }
}
