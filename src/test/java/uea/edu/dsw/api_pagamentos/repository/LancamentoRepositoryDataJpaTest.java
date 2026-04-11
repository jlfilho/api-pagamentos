package uea.edu.dsw.api_pagamentos.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import uea.edu.dsw.api_pagamentos.model.Categoria;
import uea.edu.dsw.api_pagamentos.model.Endereco;
import uea.edu.dsw.api_pagamentos.model.Lancamento;
import uea.edu.dsw.api_pagamentos.model.Pessoa;
import uea.edu.dsw.api_pagamentos.model.TipoLancamento;
import uea.edu.dsw.api_pagamentos.repository.LancamentoRepository.CategoriaTotalProjection;
import uea.edu.dsw.api_pagamentos.repository.LancamentoRepository.MesTotalProjection;

@DataJpaTest(properties = {
        "spring.sql.init.mode=never"
})
class LancamentoRepositoryDataJpaTest {

    @Autowired
    private LancamentoRepository lancamentoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @BeforeEach
    void setUp() {
        lancamentoRepository.deleteAll();
        pessoaRepository.deleteAll();
        categoriaRepository.deleteAll();
    }

    @Test
    void deveFiltrarPorDescricaoIgnorandoMaiusculasEMinusculas() {
        criarLancamento("Salario Abril", LocalDate.of(2024, 4, 10), new BigDecimal("3000.00"));
        criarLancamento("Conta de Luz", LocalDate.of(2024, 5, 15), new BigDecimal("250.00"));

        Page<Lancamento> pagina = lancamentoRepository.filtrar("sAlArIo", null, null, PageRequest.of(0, 10));

        assertEquals(1, pagina.getTotalElements());
        assertEquals("Salario Abril", pagina.getContent().get(0).getDescricao());
    }

    @Test
    void deveFiltrarPorIntervaloDeDataDeVencimento() {
        criarLancamento("Salario Abril", LocalDate.of(2024, 4, 10), new BigDecimal("3000.00"));
        criarLancamento("Conta de Luz", LocalDate.of(2024, 5, 15), new BigDecimal("250.00"));

        Page<Lancamento> pagina = lancamentoRepository.filtrar(
                null,
                LocalDate.of(2024, 5, 1),
                LocalDate.of(2024, 5, 31),
                PageRequest.of(0, 10));

        assertEquals(1, pagina.getTotalElements());
        assertEquals("Conta de Luz", pagina.getContent().get(0).getDescricao());
    }

    @Test
    void deveSomarReceitasPorCategoriaNoPeriodo() {
        criarLancamento("Salario Maio", LocalDate.of(2024, 5, 10), new BigDecimal("3000.00"), TipoLancamento.RECEITA,
                "Salario");
        criarLancamento("Freela Maio", LocalDate.of(2024, 5, 20), new BigDecimal("1000.00"), TipoLancamento.RECEITA,
                "Salario");
        criarLancamento("Dividendo Maio", LocalDate.of(2024, 5, 22), new BigDecimal("500.00"), TipoLancamento.RECEITA,
                "Investimentos");
        criarLancamento("Aluguel", LocalDate.of(2024, 5, 7), new BigDecimal("1200.00"), TipoLancamento.DESPESA,
                "Moradia");
        criarLancamento("Receita Fora Periodo", LocalDate.of(2024, 6, 1), new BigDecimal("700.00"),
                TipoLancamento.RECEITA, "Salario");

        List<CategoriaTotalProjection> categorias = lancamentoRepository.somarReceitasPorCategoria(
                LocalDate.of(2024, 5, 1),
                LocalDate.of(2024, 5, 31));

        assertEquals(2, categorias.size());
        assertEquals("Salario", categorias.get(0).getCategoria());
        assertEquals(new BigDecimal("4000.00"), categorias.get(0).getTotal());
        assertEquals("Investimentos", categorias.get(1).getCategoria());
        assertEquals(new BigDecimal("500.00"), categorias.get(1).getTotal());
    }

    @Test
    void deveSomarTotalGeralDeReceitasNoPeriodo() {
        criarLancamento("Salario Maio", LocalDate.of(2024, 5, 10), new BigDecimal("3000.00"), TipoLancamento.RECEITA,
                "Salario");
        criarLancamento("Freela Maio", LocalDate.of(2024, 5, 20), new BigDecimal("1000.00"), TipoLancamento.RECEITA,
                "Salario");
        criarLancamento("Despesa Maio", LocalDate.of(2024, 5, 7), new BigDecimal("1200.00"), TipoLancamento.DESPESA,
                "Moradia");
        criarLancamento("Receita Fora Periodo", LocalDate.of(2024, 6, 1), new BigDecimal("700.00"),
                TipoLancamento.RECEITA, "Salario");

        BigDecimal total = lancamentoRepository.somarTotalReceitas(
                LocalDate.of(2024, 5, 1),
                LocalDate.of(2024, 5, 31));

        assertEquals(new BigDecimal("4000.00"), total);
    }

    @Test
    void deveRetornarZeroQuandoNaoHouverReceitasNoPeriodo() {
        criarLancamento("Despesa Maio", LocalDate.of(2024, 5, 7), new BigDecimal("1200.00"), TipoLancamento.DESPESA,
                "Moradia");

        BigDecimal total = lancamentoRepository.somarTotalReceitas(
                LocalDate.of(2024, 5, 1),
                LocalDate.of(2024, 5, 31));

        assertEquals(BigDecimal.ZERO, total);
    }

    @Test
    void deveSomarDespesasPorCategoriaNoPeriodo() {
        criarLancamento("Aluguel Maio", LocalDate.of(2024, 5, 10), new BigDecimal("1200.00"), TipoLancamento.DESPESA,
                "Moradia");
        criarLancamento("Condominio Maio", LocalDate.of(2024, 5, 20), new BigDecimal("300.00"), TipoLancamento.DESPESA,
                "Moradia");
        criarLancamento("Mercado Maio", LocalDate.of(2024, 5, 22), new BigDecimal("500.00"), TipoLancamento.DESPESA,
                "Alimentacao");
        criarLancamento("Receita Maio", LocalDate.of(2024, 5, 7), new BigDecimal("2500.00"), TipoLancamento.RECEITA,
                "Salario");
        criarLancamento("Despesa Fora Periodo", LocalDate.of(2024, 6, 1), new BigDecimal("700.00"),
                TipoLancamento.DESPESA, "Moradia");

        List<CategoriaTotalProjection> categorias = lancamentoRepository.somarDespesasPorCategoria(
                LocalDate.of(2024, 5, 1),
                LocalDate.of(2024, 5, 31));

        assertEquals(2, categorias.size());
        assertEquals("Moradia", categorias.get(0).getCategoria());
        assertEquals(new BigDecimal("1500.00"), categorias.get(0).getTotal());
        assertEquals("Alimentacao", categorias.get(1).getCategoria());
        assertEquals(new BigDecimal("500.00"), categorias.get(1).getTotal());
    }

    @Test
    void deveSomarTotalGeralDeDespesasNoPeriodo() {
        criarLancamento("Aluguel Maio", LocalDate.of(2024, 5, 10), new BigDecimal("1200.00"), TipoLancamento.DESPESA,
                "Moradia");
        criarLancamento("Mercado Maio", LocalDate.of(2024, 5, 22), new BigDecimal("500.00"), TipoLancamento.DESPESA,
                "Alimentacao");
        criarLancamento("Receita Maio", LocalDate.of(2024, 5, 7), new BigDecimal("2500.00"), TipoLancamento.RECEITA,
                "Salario");
        criarLancamento("Despesa Fora Periodo", LocalDate.of(2024, 6, 1), new BigDecimal("700.00"),
                TipoLancamento.DESPESA, "Moradia");

        BigDecimal total = lancamentoRepository.somarTotalDespesas(
                LocalDate.of(2024, 5, 1),
                LocalDate.of(2024, 5, 31));

        assertEquals(new BigDecimal("1700.00"), total);
    }

    @Test
    void deveRetornarZeroQuandoNaoHouverDespesasNoPeriodo() {
        criarLancamento("Receita Maio", LocalDate.of(2024, 5, 7), new BigDecimal("2500.00"), TipoLancamento.RECEITA,
                "Salario");

        BigDecimal total = lancamentoRepository.somarTotalDespesas(
                LocalDate.of(2024, 5, 1),
                LocalDate.of(2024, 5, 31));

        assertEquals(BigDecimal.ZERO, total);
    }

    @Test
    void deveBuscarTop10DespesasOrdenadasPorValorNoPeriodo() {
        for (int i = 1; i <= 12; i++) {
            criarLancamento("Despesa " + i, LocalDate.of(2024, 5, 10), BigDecimal.valueOf(i * 100L),
                    TipoLancamento.DESPESA, "CatDespesa");
        }
        criarLancamento("Receita Mesmo Periodo", LocalDate.of(2024, 5, 10), new BigDecimal("9999.00"),
                TipoLancamento.RECEITA, "CatReceita");
        criarLancamento("Despesa Fora Periodo", LocalDate.of(2024, 6, 10), new BigDecimal("10000.00"),
                TipoLancamento.DESPESA, "CatDespesa");

        List<Lancamento> top10 = lancamentoRepository.buscarTopDespesasPorPeriodo(
                LocalDate.of(2024, 5, 1),
                LocalDate.of(2024, 5, 31),
                PageRequest.of(0, 10));

        assertEquals(10, top10.size());
        assertEquals(TipoLancamento.DESPESA, top10.get(0).getTipo());
        assertEquals(new BigDecimal("1200"), top10.get(0).getValor());
        assertEquals(new BigDecimal("1100"), top10.get(1).getValor());
        assertEquals(new BigDecimal("300"), top10.get(9).getValor());
    }

    @Test
    void deveBuscarTop10ReceitasOrdenadasPorValorNoPeriodo() {
        for (int i = 1; i <= 12; i++) {
            criarLancamento("Receita " + i, LocalDate.of(2024, 5, 10), BigDecimal.valueOf(i * 200L),
                    TipoLancamento.RECEITA, "CatReceita");
        }
        criarLancamento("Despesa Mesmo Periodo", LocalDate.of(2024, 5, 10), new BigDecimal("9999.00"),
                TipoLancamento.DESPESA, "CatDespesa");
        criarLancamento("Receita Fora Periodo", LocalDate.of(2024, 6, 10), new BigDecimal("10000.00"),
                TipoLancamento.RECEITA, "CatReceita");

        List<Lancamento> top10 = lancamentoRepository.buscarTopReceitasPorPeriodo(
                LocalDate.of(2024, 5, 1),
                LocalDate.of(2024, 5, 31),
                PageRequest.of(0, 10));

        assertEquals(10, top10.size());
        assertEquals(TipoLancamento.RECEITA, top10.get(0).getTipo());
        assertEquals(new BigDecimal("2400"), top10.get(0).getValor());
        assertEquals(new BigDecimal("2200"), top10.get(1).getValor());
        assertEquals(new BigDecimal("600"), top10.get(9).getValor());
    }

    @Test
    void deveSomarReceitasPagasPorMesNoPeriodo() {
        criarLancamento("Receita Jan 1", LocalDate.of(2024, 1, 5), LocalDate.of(2024, 1, 5), new BigDecimal("1000.00"),
                TipoLancamento.RECEITA, "Salario");
        criarLancamento("Receita Jan 2", LocalDate.of(2024, 1, 20), LocalDate.of(2024, 1, 20), new BigDecimal("500.00"),
                TipoLancamento.RECEITA, "Freela");
        criarLancamento("Receita Fev", LocalDate.of(2024, 2, 10), LocalDate.of(2024, 2, 11), new BigDecimal("700.00"),
                TipoLancamento.RECEITA, "Freela");
        criarLancamento("Receita Nao Paga", LocalDate.of(2024, 2, 18), null, new BigDecimal("900.00"),
                TipoLancamento.RECEITA, "Salario");
        criarLancamento("Despesa Paga", LocalDate.of(2024, 2, 5), LocalDate.of(2024, 2, 6), new BigDecimal("300.00"),
                TipoLancamento.DESPESA, "Moradia");
        criarLancamento("Receita Fora Periodo", LocalDate.of(2024, 3, 3), LocalDate.of(2024, 3, 3), new BigDecimal("800.00"),
                TipoLancamento.RECEITA, "Salario");

        List<MesTotalProjection> totais = lancamentoRepository.somarReceitasPagasPorMes(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 2, 29));

        assertEquals(2, totais.size());
        assertEquals(2024, totais.get(0).getAno());
        assertEquals(1, totais.get(0).getMes());
        assertEquals(new BigDecimal("1500.00"), totais.get(0).getTotal());
        assertEquals(2024, totais.get(1).getAno());
        assertEquals(2, totais.get(1).getMes());
        assertEquals(new BigDecimal("700.00"), totais.get(1).getTotal());
    }

    @Test
    void deveRetornarVazioQuandoNaoHouverReceitasPagasNoPeriodo() {
        criarLancamento("Receita Nao Paga", LocalDate.of(2024, 1, 10), null, new BigDecimal("900.00"),
                TipoLancamento.RECEITA, "Salario");
        criarLancamento("Despesa Paga", LocalDate.of(2024, 1, 12), LocalDate.of(2024, 1, 12), new BigDecimal("300.00"),
                TipoLancamento.DESPESA, "Moradia");

        List<MesTotalProjection> totais = lancamentoRepository.somarReceitasPagasPorMes(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 31));

        assertEquals(0, totais.size());
    }

    private void criarLancamento(String descricao, LocalDate dataVencimento, BigDecimal valor) {
        criarLancamento(descricao, dataVencimento, valor, TipoLancamento.RECEITA, "Cat " + descricao.substring(0, 3));
    }

    private void criarLancamento(String descricao, LocalDate dataVencimento, LocalDate dataPagamento, BigDecimal valor,
            TipoLancamento tipo, String nomeCategoria) {
        Categoria categoria = new Categoria();
        categoria.setNome(nomeCategoria);
        categoria = categoriaRepository.save(categoria);

        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Pessoa " + descricao);
        pessoa.setAtivo(true);
        pessoa.setEndereco(new Endereco(
                "Rua B",
                "456",
                null,
                "Centro",
                "12345-678",
                "Manaus",
                "AM"));
        pessoa = pessoaRepository.save(pessoa);

        Lancamento lancamento = new Lancamento();
        lancamento.setDescricao(descricao);
        lancamento.setValor(valor);
        lancamento.setDataVencimento(dataVencimento);
        lancamento.setDataPagamento(dataPagamento);
        lancamento.setTipo(tipo);
        lancamento.setCategoria(categoria);
        lancamento.setPessoa(pessoa);

        lancamentoRepository.save(lancamento);
    }

    private void criarLancamento(String descricao, LocalDate dataVencimento, BigDecimal valor, TipoLancamento tipo,
            String nomeCategoria) {
        Categoria categoria = new Categoria();
        categoria.setNome(nomeCategoria);
        categoria = categoriaRepository.save(categoria);

        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Pessoa " + descricao);
        pessoa.setAtivo(true);
        pessoa.setEndereco(new Endereco(
                "Rua B",
                "456",
                null,
                "Centro",
                "12345-678",
                "Manaus",
                "AM"));
        pessoa = pessoaRepository.save(pessoa);

        Lancamento lancamento = new Lancamento();
        lancamento.setDescricao(descricao);
        lancamento.setValor(valor);
        lancamento.setDataVencimento(dataVencimento);
        lancamento.setTipo(tipo);
        lancamento.setCategoria(categoria);
        lancamento.setPessoa(pessoa);

        lancamentoRepository.save(lancamento);
    }
}
