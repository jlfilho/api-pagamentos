package uea.edu.dsw.api_pagamentos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uea.edu.dsw.api_pagamentos.model.Categoria;
import uea.edu.dsw.api_pagamentos.model.TipoLancamento;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LancamentoDTO {
    private Long codigo;
    private String descricao;
    private BigDecimal valor;
    private LocalDate dataVencimento;
    private LocalDate dataPagamento;
    private String observacao;
    private TipoLancamento tipo;
    private Categoria categoria;
    private PessoaDTO pessoa;
}
