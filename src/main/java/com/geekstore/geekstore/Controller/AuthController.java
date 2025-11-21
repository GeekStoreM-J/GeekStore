package com.geekstore.geekstore.Controller;

import com.geekstore.geekstore.DTO.RegisterDTO;
import com.geekstore.geekstore.Model.User;
import com.geekstore.geekstore.Service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model, Principal principal) {
        if (principal != null) {
            return "redirect:/"; // já logado
        }
        model.addAttribute("registerDTO", new RegisterDTO("", "", "", ""));
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute RegisterDTO registerDTO, Model model) {

        if (!registerDTO.password().equals(registerDTO.confirmPassword())) {
            model.addAttribute("error", "As senhas não coincidem!");
            return "register";
        }

        if (userService.emailExists(registerDTO.email())) {
            model.addAttribute("error", "O e-mail já está cadastrado!");
            return "register";
        }

        User user = new User();
        user.setName(registerDTO.name());
        user.setEmail(registerDTO.email());
        user.setPassword(passwordEncoder.encode(registerDTO.password()));
        user.setRole("USER");

        userService.save(user);

        return "redirect:/login?registered";
    }
}
