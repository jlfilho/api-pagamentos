package uea.edu.dsw.api_pagamentos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardTopLancamentoDTO {
    private Long codigo;
    private String descricao;
    private String categoria;
    private BigDecimal valor;
    private LocalDate dataVencimento;
    private LocalDate dataPagamento;
}
