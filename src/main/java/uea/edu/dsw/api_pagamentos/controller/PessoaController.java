package uea.edu.dsw.api_pagamentos.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import uea.edu.dsw.api_pagamentos.dto.PessoaDTO;
import uea.edu.dsw.api_pagamentos.model.Categoria;
import uea.edu.dsw.api_pagamentos.service.PessoaService;

@RestController
@RequestMapping("/pessoas")
public class PessoaController {

    private final PessoaService pessoaService;

    public PessoaController(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
    }

    @GetMapping
    public ResponseEntity<List<PessoaDTO>> listarPessoas() {
        List<PessoaDTO> pessoas = pessoaService.listarPessoas();
        if (pessoas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pessoas);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<PessoaDTO> buscarPessoa(@PathVariable Long codigo) {
        PessoaDTO pessoa = pessoaService.buscarPessoaPorCodigo(codigo);
        return ResponseEntity.ok(pessoa);
    }

    @PostMapping
    public ResponseEntity<PessoaDTO> criarPessoa(@Valid @RequestBody PessoaDTO pessoa) {
        PessoaDTO criada = pessoaService.criarPessoa(pessoa);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{codigo}")
                .buildAndExpand(criada.getCodigo())
                .toUri();
        return ResponseEntity.created(uri).body(criada);
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<PessoaDTO> atualizarPessoa(@PathVariable Long codigo, @RequestBody PessoaDTO pessoa) {
        PessoaDTO atualizada = pessoaService.atualizarPessoa(codigo, pessoa);
        return ResponseEntity.ok(atualizada);
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> deletarPessoa(@PathVariable Long codigo) {
        pessoaService.deletarPessoa(codigo);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{codigo}/ativo")
    public ResponseEntity<PessoaDTO> atualizarStatus(@PathVariable Long codigo, @RequestBody Boolean ativo) {
        PessoaDTO atualizada = pessoaService.atualizarStatus(codigo, ativo);
        return ResponseEntity.ok(atualizada);
    }
}
