package uea.edu.dsw.api_pagamentos.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardTotalMensalDTO {
    private Integer ano;
    private Integer mes;
    private BigDecimal total;
}
