package uea.edu.dsw.api_pagamentos.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDTO {
    @NotNull(message = "O email é obrigatório.")
    String username;
    @NotNull(message = "A senha é obrigatória.")
    String password;
}