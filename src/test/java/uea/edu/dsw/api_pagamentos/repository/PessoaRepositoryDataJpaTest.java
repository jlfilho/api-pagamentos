package uea.edu.dsw.api_pagamentos.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import uea.edu.dsw.api_pagamentos.model.Endereco;
import uea.edu.dsw.api_pagamentos.model.Pessoa;

@DataJpaTest(properties = {
        "spring.sql.init.mode=never"
})
class PessoaRepositoryDataJpaTest {

    @Autowired
    private PessoaRepository pessoaRepository;

    @BeforeEach
    void setUp() {
        pessoaRepository.deleteAll();
    }

    @Test
    void deveRetornarTodasPessoasQuandoNomeForNuloOuVazio() {
        pessoaRepository.save(criarPessoa("Maria Silva"));
        pessoaRepository.save(criarPessoa("Joao Souza"));

        Page<Pessoa> paginaNomeNulo = pessoaRepository.findAllByNomeOptional(null, PageRequest.of(0, 10));
        Page<Pessoa> paginaNomeVazio = pessoaRepository.findAllByNomeOptional("   ", PageRequest.of(0, 10));

        assertEquals(2, paginaNomeNulo.getTotalElements());
        assertEquals(2, paginaNomeVazio.getTotalElements());
    }

    @Test
    void deveFiltrarPorNomeComCaseInsensitive() {
        pessoaRepository.save(criarPessoa("Maria Clara"));
        pessoaRepository.save(criarPessoa("Pedro Oliveira"));

        Page<Pessoa> pagina = pessoaRepository.findAllByNomeOptional("mArIa", PageRequest.of(0, 10));

        assertEquals(1, pagina.getTotalElements());
        assertEquals("Maria Clara", pagina.getContent().get(0).getNome());
    }

    private Pessoa criarPessoa(String nome) {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(nome);
        pessoa.setAtivo(true);
        pessoa.setEndereco(new Endereco(
                "Rua A",
                "123",
                null,
                "Centro",
                "12345-678",
                "Manaus",
                "AM"));
        return pessoa;
    }
}