package uea.edu.dsw.api_pagamentos.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import uea.edu.dsw.api_pagamentos.dto.DashboardCategoriaTotalDTO;
import uea.edu.dsw.api_pagamentos.dto.DashboardFiltroDTO;
import uea.edu.dsw.api_pagamentos.dto.DashboardResponseDTO;
import uea.edu.dsw.api_pagamentos.dto.DashboardResumoTipoDTO;
import uea.edu.dsw.api_pagamentos.dto.DashboardTopLancamentoDTO;
import uea.edu.dsw.api_pagamentos.dto.DashboardTotalMensalDTO;
import uea.edu.dsw.api_pagamentos.model.Lancamento;
import uea.edu.dsw.api_pagamentos.repository.LancamentoRepository;
import uea.edu.dsw.api_pagamentos.repository.LancamentoRepository.CategoriaTotalProjection;
import uea.edu.dsw.api_pagamentos.repository.LancamentoRepository.MesTotalProjection;

@Service
public class DashboardService {

    private static final int TOP_LIMIT = 10;

    private final LancamentoRepository lancamentoRepository;

    public DashboardService(LancamentoRepository lancamentoRepository) {
        this.lancamentoRepository = lancamentoRepository;
    }

    public DashboardResponseDTO consultarDashboard(DashboardFiltroDTO filtro) {
        LocalDate dataInicio = filtro.getDataInicio();
        LocalDate dataFim = filtro.getDataFim();

        DashboardResumoTipoDTO receitas = new DashboardResumoTipoDTO(
                nvl(lancamentoRepository.somarTotalReceitas(dataInicio, dataFim)),
                mapCategorias(lancamentoRepository.somarReceitasPorCategoria(dataInicio, dataFim)));

        DashboardResumoTipoDTO despesas = new DashboardResumoTipoDTO(
                nvl(lancamentoRepository.somarTotalDespesas(dataInicio, dataFim)),
                mapCategorias(lancamentoRepository.somarDespesasPorCategoria(dataInicio, dataFim)));

        List<DashboardTopLancamentoDTO> top10Despesas = mapTopLancamentos(
                lancamentoRepository.buscarTopDespesasPorPeriodo(dataInicio, dataFim, PageRequest.of(0, TOP_LIMIT)));
        List<DashboardTopLancamentoDTO> top10Receitas = mapTopLancamentos(
                lancamentoRepository.buscarTopReceitasPorPeriodo(dataInicio, dataFim, PageRequest.of(0, TOP_LIMIT)));

        List<DashboardTotalMensalDTO> receitasPagasMensal = montarSerieMensal(
                dataInicio,
                dataFim,
                lancamentoRepository.somarReceitasPagasPorMes(dataInicio, dataFim));

        List<DashboardTotalMensalDTO> despesasPagasMensal = montarSerieMensal(
                dataInicio,
                dataFim,
                lancamentoRepository.somarDespesasPagasPorMes(dataInicio, dataFim));

        return new DashboardResponseDTO(
                dataInicio,
                dataFim,
                receitas,
                despesas,
                top10Despesas,
                top10Receitas,
                receitasPagasMensal,
                despesasPagasMensal);
    }

    private List<DashboardCategoriaTotalDTO> mapCategorias(List<CategoriaTotalProjection> projections) {
        List<DashboardCategoriaTotalDTO> categorias = new ArrayList<>();
        for (CategoriaTotalProjection projection : projections) {
            categorias.add(new DashboardCategoriaTotalDTO(
                    projection.getCategoria(),
                    nvl(projection.getTotal())));
        }
        return categorias;
    }

    private List<DashboardTopLancamentoDTO> mapTopLancamentos(List<Lancamento> lancamentos) {
        List<DashboardTopLancamentoDTO> topLancamentos = new ArrayList<>();
        for (Lancamento lancamento : lancamentos) {
            topLancamentos.add(new DashboardTopLancamentoDTO(
                    lancamento.getCodigo(),
                    lancamento.getDescricao(),
                    lancamento.getCategoria() != null ? lancamento.getCategoria().getNome() : null,
                    nvl(lancamento.getValor()),
                    lancamento.getDataVencimento(),
                    lancamento.getDataPagamento()));
        }
        return topLancamentos;
    }

    private List<DashboardTotalMensalDTO> montarSerieMensal(
            LocalDate dataInicio,
            LocalDate dataFim,
            List<MesTotalProjection> projections) {
        if (dataInicio == null || dataFim == null) {
            return Collections.emptyList();
        }

        Map<YearMonth, BigDecimal> totaisPorMes = new HashMap<>();
        for (MesTotalProjection projection : projections) {
            YearMonth anoMes = YearMonth.of(projection.getAno(), projection.getMes());
            totaisPorMes.put(anoMes, nvl(projection.getTotal()));
        }

        List<DashboardTotalMensalDTO> serie = new ArrayList<>();
        YearMonth atual = YearMonth.from(dataInicio);
        YearMonth limite = YearMonth.from(dataFim);

        while (!atual.isAfter(limite)) {
            serie.add(new DashboardTotalMensalDTO(
                    atual.getYear(),
                    atual.getMonthValue(),
                    totaisPorMes.getOrDefault(atual, BigDecimal.ZERO)));
            atual = atual.plusMonths(1);
        }

        return serie;
    }

    private BigDecimal nvl(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }
}
