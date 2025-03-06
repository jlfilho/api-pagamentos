package uea.edu.dsw.api_pagamentos.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import uea.edu.dsw.api_pagamentos.dto.LancamentoDTO;
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
    public ResponseEntity<List<LancamentoDTO>> listarLancamentos() {
        List<LancamentoDTO> lancamentos = lancamentoService.listarLancamentos();
        if (lancamentos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lancamentos);
    }

    // GET /lancamentos/{codigo}
    @GetMapping("/{codigo}")
    public ResponseEntity<LancamentoDTO> buscarLancamento(@PathVariable Long codigo) {
        LancamentoDTO lancamento = lancamentoService.buscarLancamentoPorCodigo(codigo);
        return ResponseEntity.ok(lancamento);
    }

    // POST /lancamentos
    @PostMapping
    public ResponseEntity<LancamentoDTO> criarLancamento(@RequestBody LancamentoDTO lancamentoDTO) {
        LancamentoDTO lancamentoCriado = lancamentoService.criarLancamento(lancamentoDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{codigo}")
                .buildAndExpand(lancamentoCriado.getCodigo())
                .toUri();
        return ResponseEntity.created(uri).body(lancamentoCriado);
    }

    // PUT /lancamentos/{codigo}
    @PutMapping("/{codigo}")
    public ResponseEntity<LancamentoDTO> atualizarLancamento(@PathVariable Long codigo,
            @RequestBody LancamentoDTO lancamentoDTO) {
        LancamentoDTO lancamentoAtualizado = lancamentoService.atualizarLancamento(codigo, lancamentoDTO);
        return ResponseEntity.ok(lancamentoAtualizado);
    }

    // DELETE /lancamentos/{codigo}
    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> deletarLancamento(@PathVariable Long codigo) {
        lancamentoService.deletarLancamento(codigo);
        return ResponseEntity.noContent().build();
    }
}
