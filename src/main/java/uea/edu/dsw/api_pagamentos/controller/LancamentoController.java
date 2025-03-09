package uea.edu.dsw.api_pagamentos.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import uea.edu.dsw.api_pagamentos.dto.LancamentoDTO;
import uea.edu.dsw.api_pagamentos.dto.LancamentoFilterDTO;
import uea.edu.dsw.api_pagamentos.dto.ResumoLancamentoDTO;
import uea.edu.dsw.api_pagamentos.service.LancamentoService;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoController {

    private final LancamentoService lancamentoService;

    public LancamentoController(LancamentoService lancamentoService) {
        this.lancamentoService = lancamentoService;
    }

     // GET /lancamentos
     @GetMapping
     @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
     public ResponseEntity<Page<LancamentoDTO>> pesquisar(LancamentoFilterDTO lancamentoFilter, Pageable pageable) {
         Page<LancamentoDTO> lancamentos = lancamentoService.pesquisar(lancamentoFilter, pageable);
         return ResponseEntity.ok(lancamentos);
     }

    // GET /lancamentos/resumo
    @GetMapping("/resumo")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Page<ResumoLancamentoDTO>> resumir(LancamentoFilterDTO lancamentoFilter, Pageable pageable) {
        Page<ResumoLancamentoDTO> lancamentos = lancamentoService.resumir(lancamentoFilter, pageable);
        return ResponseEntity.ok(lancamentos);
    }

    // GET /lancamentos/{codigo}
    @GetMapping("/{codigo}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<LancamentoDTO> buscarLancamento(@PathVariable Long codigo) {
        LancamentoDTO lancamento = lancamentoService.buscarLancamentoPorCodigo(codigo);
        return ResponseEntity.ok(lancamento);
    }

    // POST /lancamentos
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<LancamentoDTO> criarLancamento(@Valid @RequestBody LancamentoDTO lancamentoDTO) {
        LancamentoDTO lancamentoCriado = lancamentoService.criarLancamento(lancamentoDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{codigo}")
                .buildAndExpand(lancamentoCriado.getCodigo())
                .toUri();
        return ResponseEntity.created(uri).body(lancamentoCriado);
    }

    // PUT /lancamentos/{codigo}
    @PutMapping("/{codigo}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<LancamentoDTO> atualizarLancamento(@Valid @PathVariable Long codigo,
            @RequestBody LancamentoDTO lancamentoDTO) {
        LancamentoDTO lancamentoAtualizado = lancamentoService.atualizarLancamento(codigo, lancamentoDTO);
        return ResponseEntity.ok(lancamentoAtualizado);
    }

    // DELETE /lancamentos/{codigo}
    @DeleteMapping("/{codigo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarLancamento(@PathVariable Long codigo) {
        lancamentoService.deletarLancamento(codigo);
        return ResponseEntity.noContent().build();
    }
}
