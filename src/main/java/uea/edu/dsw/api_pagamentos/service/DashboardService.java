package uea.edu.dsw.api_pagamentos.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import org.springframework.stereotype.Service;

import uea.edu.dsw.api_pagamentos.dto.DashboardResponseDTO;
import uea.edu.dsw.api_pagamentos.dto.DashboardResumoTipoDTO;

@Service
public class DashboardService {

    public DashboardResponseDTO consultarDashboard(LocalDate dataInicio, LocalDate dataFim) {
        DashboardResumoTipoDTO resumoVazio = new DashboardResumoTipoDTO(BigDecimal.ZERO, Collections.emptyList());

        return new DashboardResponseDTO(
                dataInicio,
                dataFim,
                resumoVazio,
                resumoVazio,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList());
    }
}
