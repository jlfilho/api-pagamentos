package uea.edu.dsw.api_pagamentos.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    private void criarLancamento(String descricao, LocalDate dataVencimento, BigDecimal valor) {
        Categoria categoria = new Categoria();
        categoria.setNome("Cat " + descricao.substring(0, 3));
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
        lancamento.setTipo(TipoLancamento.RECEITA);
        lancamento.setCategoria(categoria);
        lancamento.setPessoa(pessoa);

        lancamentoRepository.save(lancamento);
    }
}
