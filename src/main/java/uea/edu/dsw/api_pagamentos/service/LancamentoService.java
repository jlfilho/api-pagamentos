package uea.edu.dsw.api_pagamentos.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uea.edu.dsw.api_pagamentos.dto.LancamentoDTO;
import uea.edu.dsw.api_pagamentos.dto.LancamentoFilterDTO;
import uea.edu.dsw.api_pagamentos.dto.ResumoLancamentoDTO;
import uea.edu.dsw.api_pagamentos.model.Categoria;
import uea.edu.dsw.api_pagamentos.model.Lancamento;
import uea.edu.dsw.api_pagamentos.model.Pessoa;
import uea.edu.dsw.api_pagamentos.repository.LancamentoRepository;
import uea.edu.dsw.api_pagamentos.service.exception.RecursoEmUsoException;
import uea.edu.dsw.api_pagamentos.service.exception.RecursoNaoEncontradoException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LancamentoService {

    private final LancamentoRepository lancamentoRepository;

    public LancamentoService(LancamentoRepository lancamentoRepository) {
        this.lancamentoRepository = lancamentoRepository;
    }

    // Método para converter Lancamento em LancamentoDTO
    private LancamentoDTO toDTO(Lancamento lancamento) {
        LancamentoDTO dto = new LancamentoDTO();
        dto.setCodigo(lancamento.getCodigo());
        dto.setDescricao(lancamento.getDescricao());
        dto.setValor(lancamento.getValor());
        dto.setDataVencimento(lancamento.getDataVencimento());
        dto.setDataPagamento(lancamento.getDataPagamento());
        dto.setObservacao(lancamento.getObservacao());
        dto.setTipo(lancamento.getTipo());
        dto.setCategoria(lancamento.getCategoria());
        dto.setPessoa(lancamento.getPessoa());
        return dto;
    }

    private ResumoLancamentoDTO toResumoDTO(Lancamento lancamento) {
        ResumoLancamentoDTO dto = new ResumoLancamentoDTO();
        dto.setCodigo(lancamento.getCodigo());
        dto.setDescricao(lancamento.getDescricao());
        dto.setValor(lancamento.getValor());
        dto.setDataVencimento(lancamento.getDataVencimento());
        dto.setDataPagamento(lancamento.getDataPagamento());
        dto.setTipo(lancamento.getTipo());
        dto.setCategoria(lancamento.getCategoria().getNome());
        dto.setPessoa(lancamento.getPessoa().getNome());
        return dto;
    }

    // Método para converter LancamentoDTO em Lancamento
    private Lancamento toEntity(LancamentoDTO dto) {
        Lancamento lancamento = new Lancamento();
        lancamento.setCodigo(dto.getCodigo());
        lancamento.setDescricao(dto.getDescricao());
        lancamento.setValor(dto.getValor());
        lancamento.setDataVencimento(dto.getDataVencimento());
        lancamento.setDataPagamento(dto.getDataPagamento());
        lancamento.setObservacao(dto.getObservacao());
        lancamento.setTipo(dto.getTipo());
        lancamento.setCategoria(dto.getCategoria());
        lancamento.setPessoa(dto.getPessoa());
        return lancamento;
    }

    @Transactional(readOnly = true)
    public Page<LancamentoDTO> pesquisar(LancamentoFilterDTO lancamentoFilter, Pageable pageable) {
        Page<Lancamento> lancamentosPage = lancamentoRepository.filtrar(
                lancamentoFilter.getDescricao(),
                lancamentoFilter.getDataVencimentoDe(),
                lancamentoFilter.getDataVencimentoAte(),
                pageable);
        return lancamentosPage.map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<ResumoLancamentoDTO> resumir(LancamentoFilterDTO lancamentoFilter, Pageable pageable) {
        Page<Lancamento> lancamentosPage = lancamentoRepository.filtrar(
                lancamentoFilter.getDescricao(),
                lancamentoFilter.getDataVencimentoDe(),
                lancamentoFilter.getDataVencimentoAte(),
                pageable);
        return lancamentosPage.map(this::toResumoDTO);
    }

    @Transactional
    public LancamentoDTO criarLancamento(LancamentoDTO lancamentoDTO) {
        Lancamento lancamento = toEntity(lancamentoDTO);
        Lancamento savedLancamento = lancamentoRepository.save(lancamento);
        return toDTO(savedLancamento);
    }

    public LancamentoDTO buscarLancamentoPorCodigo(Long codigo) {
        Lancamento lancamento = lancamentoRepository.findById(codigo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lançamento não encontrado"));
        return toDTO(lancamento);
    }

    public List<LancamentoDTO> listarLancamentos() {
        return lancamentoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public LancamentoDTO atualizarLancamento(Long codigo, LancamentoDTO lancamentoDTO) {
        Lancamento lancamentoExistente = lancamentoRepository.findById(codigo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lançamento não encontrado"));

        lancamentoExistente.setDescricao(lancamentoDTO.getDescricao());
        lancamentoExistente.setValor(lancamentoDTO.getValor());
        lancamentoExistente.setDataVencimento(lancamentoDTO.getDataVencimento());
        lancamentoExistente.setDataPagamento(lancamentoDTO.getDataPagamento());
        lancamentoExistente.setObservacao(lancamentoDTO.getObservacao());
        lancamentoExistente.setTipo(lancamentoDTO.getTipo());

        if (lancamentoDTO.getCategoria() != null) {
            Categoria categoria = new Categoria();
            categoria.setCodigo(lancamentoDTO.getCategoria().getCodigo());
            categoria.setNome(lancamentoDTO.getCategoria().getNome());
            lancamentoExistente.setCategoria(categoria);
        }
        if (lancamentoDTO.getPessoa() != null) {
            Pessoa pessoa = new Pessoa();
            pessoa.setCodigo(lancamentoDTO.getPessoa().getCodigo());
            pessoa.setNome(lancamentoDTO.getPessoa().getNome());
            lancamentoExistente.setPessoa(pessoa);
        }

        Lancamento LancamentoAtualizado = lancamentoRepository.save(lancamentoExistente);
        return toDTO(LancamentoAtualizado);
    }

    @Transactional
    public void deletarLancamento(Long codigo) {
        if (!lancamentoRepository.existsById(codigo)) {
            throw new RecursoNaoEncontradoException("Lançamento não encontrado");
        }
        try {
            lancamentoRepository.deleteById(codigo);
        } catch (Exception e) {
            throw new RecursoEmUsoException("Lançamento em uso e não pode ser removido");
        }
    }
}
