package uea.edu.dsw.api_pagamentos.dto;

import jakarta.persistence.Embedded;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uea.edu.dsw.api_pagamentos.model.Endereco;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PessoaDTO {
    private Long codigo;
    @NotNull
    @Size(min = 3, max = 50)
    private String nome;
    
    @NotNull
    private Boolean ativo;

    @NotNull(message = "O endereço é obrigatório")
    @Valid
    @Embedded
    private Endereco endereco;
}
