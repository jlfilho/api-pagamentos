package uea.edu.dsw.api_pagamentos.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uea.edu.dsw.api_pagamentos.repository.LancamentoRepository;
import uea.edu.dsw.api_pagamentos.service.exception.RecursoEmUsoException;
import uea.edu.dsw.api_pagamentos.service.exception.RecursoNaoEncontradoException;

@ExtendWith(MockitoExtension.class)
class LancamentoServiceTest {

    @Mock
    private LancamentoRepository lancamentoRepository;

    @InjectMocks
    private LancamentoService lancamentoService;

    @Test
    void deveLancarNotFoundQuandoBuscarLancamentoInexistente() {
        when(lancamentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> lancamentoService.buscarLancamentoPorCodigo(99L));
    }

    @Test
    void deveLancarNotFoundQuandoAtualizarLancamentoInexistente() {
        when(lancamentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> lancamentoService.atualizarLancamento(99L, new uea.edu.dsw.api_pagamentos.dto.LancamentoDTO()));
    }

    @Test
    void deveLancarNotFoundQuandoExcluirLancamentoInexistente() {
        when(lancamentoRepository.existsById(99L)).thenReturn(false);

        assertThrows(RecursoNaoEncontradoException.class,
                () -> lancamentoService.deletarLancamento(99L));
    }

    @Test
    void deveLancarRecursoEmUsoQuandoFalharAoExcluirLancamento() {
        when(lancamentoRepository.existsById(1L)).thenReturn(true);
        org.mockito.Mockito.doThrow(new RuntimeException("fk"))
                .when(lancamentoRepository).deleteById(1L);

        assertThrows(RecursoEmUsoException.class,
                () -> lancamentoService.deletarLancamento(1L));
    }
}
