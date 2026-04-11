package uea.edu.dsw.api_pagamentos.service;

import java.math.BigDecimal;
import java.util.Collections;

import org.springframework.stereotype.Service;

import uea.edu.dsw.api_pagamentos.dto.DashboardFiltroDTO;
import uea.edu.dsw.api_pagamentos.dto.DashboardResponseDTO;
import uea.edu.dsw.api_pagamentos.dto.DashboardResumoTipoDTO;

@Service
public class DashboardService {

    public DashboardResponseDTO consultarDashboard(DashboardFiltroDTO filtro) {
        DashboardResumoTipoDTO resumoVazio = new DashboardResumoTipoDTO(BigDecimal.ZERO, Collections.emptyList());

        return new DashboardResponseDTO(
                filtro.getDataInicio(),
                filtro.getDataFim(),
                resumoVazio,
                resumoVazio,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList());
    }
}
