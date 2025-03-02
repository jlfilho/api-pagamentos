package uea.edu.dsw.api_pagamentos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PessoaDTO {
    private Long codigo;
    private String nome;
    private Boolean ativo;
    private EnderecoDTO endereco;

}
