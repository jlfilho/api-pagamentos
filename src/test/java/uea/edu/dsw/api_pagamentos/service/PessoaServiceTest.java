package uea.edu.dsw.api_pagamentos.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import uea.edu.dsw.api_pagamentos.model.Pessoa;
import uea.edu.dsw.api_pagamentos.repository.PessoaRepository;
import uea.edu.dsw.api_pagamentos.service.exception.RecursoEmUsoException;
import uea.edu.dsw.api_pagamentos.service.exception.RecursoNaoEncontradoException;

@ExtendWith(MockitoExtension.class)
class PessoaServiceTest {

    @Mock
    private PessoaRepository pessoaRepository;

    @InjectMocks
    private PessoaService pessoaService;

    @Test
    void deveLancarNotFoundQuandoBuscarPessoaInexistente() {
        when(pessoaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> pessoaService.buscarPessoaPorCodigo(99L));
    }

    @Test
    void deveLancarConflitoQuandoAtualizarStatusComMesmoValor() {
        Pessoa pessoa = new Pessoa();
        pessoa.setCodigo(1L);
        pessoa.setAtivo(true);
        when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));

        assertThrows(IllegalArgumentException.class,
                () -> pessoaService.atualizarStatus(1L, true));
        verify(pessoaRepository, never()).save(org.mockito.ArgumentMatchers.any(Pessoa.class));
    }

    @Test
    void deveLancarRecursoEmUsoQuandoFalharAoExcluirPessoa() {
        when(pessoaRepository.existsById(1L)).thenReturn(true);
        org.mockito.Mockito.doThrow(new DataIntegrityViolationException("fk"))
                .when(pessoaRepository).deleteById(1L);

        assertThrows(RecursoEmUsoException.class,
                () -> pessoaService.deletarPessoa(1L));
    }
}
