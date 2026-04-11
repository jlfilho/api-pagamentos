package uea.edu.dsw.api_pagamentos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import uea.edu.dsw.api_pagamentos.dto.DashboardFiltroDTO;
import uea.edu.dsw.api_pagamentos.dto.DashboardResponseDTO;
import uea.edu.dsw.api_pagamentos.model.Categoria;
import uea.edu.dsw.api_pagamentos.model.Lancamento;
import uea.edu.dsw.api_pagamentos.repository.LancamentoRepository;
import uea.edu.dsw.api_pagamentos.repository.LancamentoRepository.CategoriaTotalProjection;
import uea.edu.dsw.api_pagamentos.repository.LancamentoRepository.MesTotalProjection;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private LancamentoRepository lancamentoRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void deveOrquestrarDashboardComDadosDoRepositorio() {
        DashboardFiltroDTO filtro = new DashboardFiltroDTO(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 3, 31),
                null,
                null);

        when(lancamentoRepository.somarTotalReceitas(filtro.getDataInicio(), filtro.getDataFim()))
                .thenReturn(new BigDecimal("1500.00"));
        when(lancamentoRepository.somarReceitasPorCategoria(filtro.getDataInicio(), filtro.getDataFim()))
                .thenReturn(List.of(categoriaTotal("Salario", "1000.00"), categoriaTotal("Freela", "500.00")));

        when(lancamentoRepository.somarTotalDespesas(filtro.getDataInicio(), filtro.getDataFim()))
                .thenReturn(new BigDecimal("700.00"));
        when(lancamentoRepository.somarDespesasPorCategoria(filtro.getDataInicio(), filtro.getDataFim()))
                .thenReturn(List.of(categoriaTotal("Moradia", "700.00")));

        when(lancamentoRepository.buscarTopDespesasPorPeriodo(
                org.mockito.ArgumentMatchers.eq(filtro.getDataInicio()),
                org.mockito.ArgumentMatchers.eq(filtro.getDataFim()),
                org.mockito.ArgumentMatchers.any(Pageable.class)))
                .thenReturn(List.of(lancamento(1L, "Aluguel", "Moradia", "700.00")));

        when(lancamentoRepository.buscarTopReceitasPorPeriodo(
                org.mockito.ArgumentMatchers.eq(filtro.getDataInicio()),
                org.mockito.ArgumentMatchers.eq(filtro.getDataFim()),
                org.mockito.ArgumentMatchers.any(Pageable.class)))
                .thenReturn(List.of(lancamento(2L, "Salario", "Salario", "1000.00")));

        when(lancamentoRepository.somarReceitasPagasPorMes(filtro.getDataInicio(), filtro.getDataFim()))
                .thenReturn(List.of(mesTotal(2024, 1, "1000.00"), mesTotal(2024, 3, "500.00")));
        when(lancamentoRepository.somarDespesasPagasPorMes(filtro.getDataInicio(), filtro.getDataFim()))
                .thenReturn(List.of(mesTotal(2024, 1, "700.00")));

        DashboardResponseDTO resposta = dashboardService.consultarDashboard(filtro);

        assertEquals(new BigDecimal("1500.00"), resposta.getReceitas().getTotal());
        assertEquals(2, resposta.getReceitas().getPorCategoria().size());
        assertEquals(new BigDecimal("700.00"), resposta.getDespesas().getTotal());
        assertEquals(1, resposta.getDespesas().getPorCategoria().size());

        assertEquals(1, resposta.getTop10Despesas().size());
        assertEquals("Aluguel", resposta.getTop10Despesas().get(0).getDescricao());
        assertEquals(1, resposta.getTop10Receitas().size());
        assertEquals("Salario", resposta.getTop10Receitas().get(0).getDescricao());

        assertEquals(3, resposta.getReceitasPagasMensal().size());
        assertEquals(new BigDecimal("1000.00"), resposta.getReceitasPagasMensal().get(0).getTotal());
        assertEquals(BigDecimal.ZERO, resposta.getReceitasPagasMensal().get(1).getTotal());
        assertEquals(new BigDecimal("500.00"), resposta.getReceitasPagasMensal().get(2).getTotal());

        assertEquals(3, resposta.getDespesasPagasMensal().size());
        assertEquals(new BigDecimal("700.00"), resposta.getDespesasPagasMensal().get(0).getTotal());
        assertEquals(BigDecimal.ZERO, resposta.getDespesasPagasMensal().get(1).getTotal());
        assertEquals(BigDecimal.ZERO, resposta.getDespesasPagasMensal().get(2).getTotal());
    }

    @Test
    void deveRetornarZerosQuandoRepositoriosRetornaremSemDados() {
        DashboardFiltroDTO filtro = new DashboardFiltroDTO(
                LocalDate.of(2024, 2, 1),
                LocalDate.of(2024, 2, 29),
                null,
                null);

        when(lancamentoRepository.somarTotalReceitas(filtro.getDataInicio(), filtro.getDataFim())).thenReturn(null);
        when(lancamentoRepository.somarReceitasPorCategoria(filtro.getDataInicio(), filtro.getDataFim()))
                .thenReturn(Collections.emptyList());
        when(lancamentoRepository.somarTotalDespesas(filtro.getDataInicio(), filtro.getDataFim())).thenReturn(null);
        when(lancamentoRepository.somarDespesasPorCategoria(filtro.getDataInicio(), filtro.getDataFim()))
                .thenReturn(Collections.emptyList());
        when(lancamentoRepository.buscarTopDespesasPorPeriodo(
                org.mockito.ArgumentMatchers.eq(filtro.getDataInicio()),
                org.mockito.ArgumentMatchers.eq(filtro.getDataFim()),
                org.mockito.ArgumentMatchers.any(Pageable.class)))
                .thenReturn(Collections.emptyList());
        when(lancamentoRepository.buscarTopReceitasPorPeriodo(
                org.mockito.ArgumentMatchers.eq(filtro.getDataInicio()),
                org.mockito.ArgumentMatchers.eq(filtro.getDataFim()),
                org.mockito.ArgumentMatchers.any(Pageable.class)))
                .thenReturn(Collections.emptyList());
        when(lancamentoRepository.somarReceitasPagasPorMes(filtro.getDataInicio(), filtro.getDataFim()))
                .thenReturn(Collections.emptyList());
        when(lancamentoRepository.somarDespesasPagasPorMes(filtro.getDataInicio(), filtro.getDataFim()))
                .thenReturn(Collections.emptyList());

        DashboardResponseDTO resposta = dashboardService.consultarDashboard(filtro);

        assertEquals(BigDecimal.ZERO, resposta.getReceitas().getTotal());
        assertEquals(BigDecimal.ZERO, resposta.getDespesas().getTotal());
        assertEquals(0, resposta.getTop10Despesas().size());
        assertEquals(0, resposta.getTop10Receitas().size());
        assertEquals(1, resposta.getReceitasPagasMensal().size());
        assertEquals(BigDecimal.ZERO, resposta.getReceitasPagasMensal().get(0).getTotal());
        assertEquals(1, resposta.getDespesasPagasMensal().size());
        assertEquals(BigDecimal.ZERO, resposta.getDespesasPagasMensal().get(0).getTotal());
    }

    private CategoriaTotalProjection categoriaTotal(String categoria, String total) {
        return new CategoriaTotalProjection() {
            @Override
            public String getCategoria() {
                return categoria;
            }

            @Override
            public BigDecimal getTotal() {
                return new BigDecimal(total);
            }
        };
    }

    private MesTotalProjection mesTotal(int ano, int mes, String total) {
        return new MesTotalProjection() {
            @Override
            public Integer getAno() {
                return ano;
            }

            @Override
            public Integer getMes() {
                return mes;
            }

            @Override
            public BigDecimal getTotal() {
                return new BigDecimal(total);
            }
        };
    }

    private Lancamento lancamento(Long codigo, String descricao, String categoriaNome, String valor) {
        Categoria categoria = new Categoria();
        categoria.setNome(categoriaNome);

        Lancamento lancamento = new Lancamento();
        lancamento.setCodigo(codigo);
        lancamento.setDescricao(descricao);
        lancamento.setCategoria(categoria);
        lancamento.setValor(new BigDecimal(valor));
        return lancamento;
    }
}
