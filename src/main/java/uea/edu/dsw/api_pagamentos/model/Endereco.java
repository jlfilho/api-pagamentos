package uea.edu.dsw.api_pagamentos.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Endereco {
   
   @NotBlank(message = "O logradouro é obrigatório")
    @Size(min = 3, max = 255, message = "O logradouro deve ter entre 5 e 255 caracteres")
    private String logradouro;

    @NotBlank(message = "O número é obrigatório")
    @Size(max = 10, message = "O número não pode exceder 10 caracteres")
    private String numero;

    @Size(max = 255, message = "O complemento não pode exceder 255 caracteres")
    private String complemento;

    @NotBlank(message = "O bairro é obrigatório")
    @Size(min = 3, max = 100, message = "O bairro deve ter entre 3 e 100 caracteres")
    private String bairro;

    @NotBlank(message = "O CEP é obrigatório")
    @Pattern(regexp = "\\d{5}-\\d{3}", message = "O CEP deve seguir o formato 12345-678")
    private String cep;

    @NotBlank(message = "A cidade é obrigatória")
    @Size(min = 3, max = 100, message = "A cidade deve ter entre 3 e 100 caracteres")
    private String cidade;

    @NotBlank(message = "O estado é obrigatório")
    @Size(min = 2, max = 2, message = "O estado deve ter 2 caracteres (sigla)")
    private String estado;
}
