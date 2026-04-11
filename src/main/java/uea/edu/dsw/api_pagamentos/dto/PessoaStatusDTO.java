package uea.edu.dsw.api_pagamentos.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PessoaStatusDTO {
    @NotNull(message = "O campo 'ativo' e obrigatorio")
    private Boolean ativo;
}
