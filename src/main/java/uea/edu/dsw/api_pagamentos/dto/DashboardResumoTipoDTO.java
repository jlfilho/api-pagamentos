package uea.edu.dsw.api_pagamentos.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResumoTipoDTO {
    private BigDecimal total;
    private List<DashboardCategoriaTotalDTO> porCategoria;
}
