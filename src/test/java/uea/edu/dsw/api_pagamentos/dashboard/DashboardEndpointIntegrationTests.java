package uea.edu.dsw.api_pagamentos.dashboard;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import uea.edu.dsw.api_pagamentos.model.Categoria;
import uea.edu.dsw.api_pagamentos.model.Endereco;
import uea.edu.dsw.api_pagamentos.model.Lancamento;
import uea.edu.dsw.api_pagamentos.model.Pessoa;
import uea.edu.dsw.api_pagamentos.model.TipoLancamento;
import uea.edu.dsw.api_pagamentos.repository.CategoriaRepository;
import uea.edu.dsw.api_pagamentos.repository.LancamentoRepository;
import uea.edu.dsw.api_pagamentos.repository.PessoaRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:gerenciador_pagamentos_dashboard_endpoint_tests",
        "spring.sql.init.mode=never",
        "security.jwt.secret-key=3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b",
        "security.jwt.expiration-time=3600000"
})
class DashboardEndpointIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LancamentoRepository lancamentoRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @BeforeEach
    void setUp() {
        lancamentoRepository.deleteAll();
        pessoaRepository.deleteAll();
        categoriaRepository.deleteAll();

        Categoria salario = categoriaRepository.save(new Categoria(null, "Salario"));
        Categoria investimentos = categoriaRepository.save(new Categoria(null, "Investimentos"));
        Categoria moradia = categoriaRepository.save(new Categoria(null, "Moradia"));
        Categoria alimentacao = categoriaRepository.save(new Categoria(null, "Alimentacao"));
        Categoria transporte = categoriaRepository.save(new Categoria(null, "Transporte"));

        Pessoa pessoa = pessoaRepository.save(new Pessoa(
                null,
                "Pessoa Teste",
                true,
                new Endereco("Rua Teste", "123", null, "Centro", "12345-678", "Manaus", "AM")));

        salvarLancamento("R1", "1000.00", LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 10),
                TipoLancamento.RECEITA, salario, pessoa);
        salvarLancamento("R2", "500.00", LocalDate.of(2024, 2, 10), null,
                TipoLancamento.RECEITA, salario, pessoa);
        salvarLancamento("R3", "700.00", LocalDate.of(2024, 2, 12), LocalDate.of(2024, 2, 12),
                TipoLancamento.RECEITA, investimentos, pessoa);
        salvarLancamento("R4", "300.00", LocalDate.of(2024, 3, 5), LocalDate.of(2024, 3, 5),
                TipoLancamento.RECEITA, investimentos, pessoa);

        salvarLancamento("D1", "400.00", LocalDate.of(2024, 1, 15), LocalDate.of(2024, 1, 16),
                TipoLancamento.DESPESA, moradia, pessoa);
        salvarLancamento("D2", "250.00", LocalDate.of(2024, 2, 20), null,
                TipoLancamento.DESPESA, alimentacao, pessoa);
        salvarLancamento("D3", "150.00", LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 2),
                TipoLancamento.DESPESA, moradia, pessoa);
        salvarLancamento("D4", "100.00", LocalDate.of(2024, 3, 10), LocalDate.of(2024, 3, 10),
                TipoLancamento.DESPESA, transporte, pessoa);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarDashboardComValoresConsolidadosParaGraficos() throws Exception {
        mockMvc.perform(get("/dashboard")
                        .param("dataInicio", "2024-01-01")
                        .param("dataFim", "2024-03-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.receitas.total").value(2500.00))
                .andExpect(jsonPath("$.receitas.porCategoria.length()").value(2))
                .andExpect(jsonPath("$.receitas.porCategoria[0].categoria").value("Salario"))
                .andExpect(jsonPath("$.receitas.porCategoria[0].total").value(1500.00))
                .andExpect(jsonPath("$.receitas.porCategoria[1].categoria").value("Investimentos"))
                .andExpect(jsonPath("$.receitas.porCategoria[1].total").value(1000.00))
                .andExpect(jsonPath("$.despesas.total").value(900.00))
                .andExpect(jsonPath("$.despesas.porCategoria.length()").value(3))
                .andExpect(jsonPath("$.despesas.porCategoria[0].categoria").value("Moradia"))
                .andExpect(jsonPath("$.despesas.porCategoria[0].total").value(550.00))
                .andExpect(jsonPath("$.despesas.porCategoria[1].categoria").value("Alimentacao"))
                .andExpect(jsonPath("$.despesas.porCategoria[1].total").value(250.00))
                .andExpect(jsonPath("$.despesas.porCategoria[2].categoria").value("Transporte"))
                .andExpect(jsonPath("$.despesas.porCategoria[2].total").value(100.00))
                .andExpect(jsonPath("$.top10Despesas.length()").value(4))
                .andExpect(jsonPath("$.top10Despesas[0].valor").value(400.00))
                .andExpect(jsonPath("$.top10Despesas[1].valor").value(250.00))
                .andExpect(jsonPath("$.top10Despesas[2].valor").value(150.00))
                .andExpect(jsonPath("$.top10Despesas[3].valor").value(100.00))
                .andExpect(jsonPath("$.top10Receitas.length()").value(4))
                .andExpect(jsonPath("$.top10Receitas[0].valor").value(1000.00))
                .andExpect(jsonPath("$.top10Receitas[1].valor").value(700.00))
                .andExpect(jsonPath("$.top10Receitas[2].valor").value(500.00))
                .andExpect(jsonPath("$.top10Receitas[3].valor").value(300.00))
                .andExpect(jsonPath("$.receitasPagasMensal.length()").value(3))
                .andExpect(jsonPath("$.receitasPagasMensal[0].mes").value(1))
                .andExpect(jsonPath("$.receitasPagasMensal[0].total").value(1000.00))
                .andExpect(jsonPath("$.receitasPagasMensal[1].mes").value(2))
                .andExpect(jsonPath("$.receitasPagasMensal[1].total").value(700.00))
                .andExpect(jsonPath("$.receitasPagasMensal[2].mes").value(3))
                .andExpect(jsonPath("$.receitasPagasMensal[2].total").value(300.00))
                .andExpect(jsonPath("$.despesasPagasMensal.length()").value(3))
                .andExpect(jsonPath("$.despesasPagasMensal[0].mes").value(1))
                .andExpect(jsonPath("$.despesasPagasMensal[0].total").value(400.00))
                .andExpect(jsonPath("$.despesasPagasMensal[1].mes").value(2))
                .andExpect(jsonPath("$.despesasPagasMensal[1].total").value(0))
                .andExpect(jsonPath("$.despesasPagasMensal[2].mes").value(3))
                .andExpect(jsonPath("$.despesasPagasMensal[2].total").value(250.00));
    }

    private void salvarLancamento(String descricao, String valor, LocalDate dataVencimento, LocalDate dataPagamento,
            TipoLancamento tipo, Categoria categoria, Pessoa pessoa) {
        Lancamento lancamento = new Lancamento();
        lancamento.setDescricao(descricao);
        lancamento.setValor(new BigDecimal(valor));
        lancamento.setDataVencimento(dataVencimento);
        lancamento.setDataPagamento(dataPagamento);
        lancamento.setTipo(tipo);
        lancamento.setCategoria(categoria);
        lancamento.setPessoa(pessoa);
        lancamentoRepository.save(lancamento);
    }
}
