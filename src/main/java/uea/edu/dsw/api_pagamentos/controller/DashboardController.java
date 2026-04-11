package uea.edu.dsw.api_pagamentos.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import uea.edu.dsw.api_pagamentos.dto.DashboardFiltroDTO;
import uea.edu.dsw.api_pagamentos.dto.DashboardResponseDTO;
import uea.edu.dsw.api_pagamentos.service.DashboardService;

@RestController
@RequestMapping("/dashboard")
@Tag(name = "Dashboard", description = "Consultas consolidadas para graficos e indicadores")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(
            summary = "Consultar dados consolidados do dashboard",
            description = "Retorna totais por categoria, top 10 e series mensais de receitas/despesas para o periodo informado.")
    @ApiResponse(
            responseCode = "200",
            description = "Dashboard gerado com sucesso")
    @ApiResponse(
            responseCode = "400",
            description = "Parametros invalidos",
            content = @Content(schema = @Schema(implementation = uea.edu.dsw.api_pagamentos.service.exception.ErrorResponse.class)))
    @ApiResponse(
            responseCode = "401",
            description = "Nao autenticado")
    @ApiResponse(
            responseCode = "403",
            description = "Sem permissao")
    public ResponseEntity<DashboardResponseDTO> consultarDashboard(@Valid DashboardFiltroDTO filtro) {
        DashboardResponseDTO response = dashboardService.consultarDashboard(filtro);
        return ResponseEntity.ok(response);
    }
}
