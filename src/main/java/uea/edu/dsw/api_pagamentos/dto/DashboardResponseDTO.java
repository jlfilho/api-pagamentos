package uea.edu.dsw.api_pagamentos.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponseDTO {
    private LocalDate periodoInicio;
    private LocalDate periodoFim;
    private DashboardResumoTipoDTO receitas;
    private DashboardResumoTipoDTO despesas;
    private List<DashboardTopLancamentoDTO> top10Despesas;
    private List<DashboardTopLancamentoDTO> top10Receitas;
    private List<DashboardTotalMensalDTO> receitasPagasMensal;
    private List<DashboardTotalMensalDTO> despesasPagasMensal;
}
