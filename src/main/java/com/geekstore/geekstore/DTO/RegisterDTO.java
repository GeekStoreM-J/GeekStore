package com.geekstore.geekstore.DTO;

public record RegisterDTO(
        String name,
        String email,
        String password,
        String confirmPassword
) {}
