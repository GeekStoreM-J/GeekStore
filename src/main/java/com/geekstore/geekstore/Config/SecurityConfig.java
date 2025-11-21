package com.geekstore.geekstore.Config;

import com.geekstore.geekstore.Service.UserSecurityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

        private final UserSecurityService userSecurityService;

        public SecurityConfig(UserSecurityService userSecurityService) {
                this.userSecurityService = userSecurityService;
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                // rotas públicas
                                                .requestMatchers("/", "/home", "/login", "/register", "/css/**",
                                                                "/js/**")
                                                .permitAll()
                                                // admin protegido
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                // checkout protegido: precisa estar logado
                                                .requestMatchers("/cart/checkout").authenticated()
                                                // qualquer outra rota é acessível
                                                .anyRequest().permitAll())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .defaultSuccessUrl("/", true)
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/")
                                                .permitAll())
                                .userDetailsService(userSecurityService)
                                .csrf(csrf -> csrf.disable()); // desativado apenas para testes

                return http.build();
        }

        // Password encoder
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        // AuthenticationManager necessário para login programático
        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }
}
