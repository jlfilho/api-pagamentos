package uea.edu.dsw.api_pagamentos.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardFiltroDTO {

    @NotNull(message = "O parametro 'dataInicio' e obrigatorio.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataInicio;

    @NotNull(message = "O parametro 'dataFim' e obrigatorio.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataFim;

    private Integer ano;

    private Boolean somentePagos;

    @AssertTrue(message = "O parametro 'dataInicio' deve ser menor ou igual ao parametro 'dataFim'.")
    public boolean isIntervaloValido() {
        if (dataInicio == null || dataFim == null) {
            return true;
        }
        return !dataInicio.isAfter(dataFim);
    }
}
