package uea.edu.dsw.api_pagamentos.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardCategoriaTotalDTO {
    private String categoria;
    private BigDecimal total;
}
