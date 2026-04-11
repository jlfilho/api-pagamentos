package uea.edu.dsw.api_pagamentos.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import uea.edu.dsw.api_pagamentos.dto.DashboardFiltroDTO;
import uea.edu.dsw.api_pagamentos.dto.DashboardResponseDTO;
import uea.edu.dsw.api_pagamentos.service.DashboardService;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<DashboardResponseDTO> consultarDashboard(@Valid DashboardFiltroDTO filtro) {
        DashboardResponseDTO response = dashboardService.consultarDashboard(filtro);
        return ResponseEntity.ok(response);
    }
}
