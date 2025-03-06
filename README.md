# Tutorial: Implementar Endpoints de Lançamento

Este tutorial orienta a implementação dos endpoints para gerenciamento de lançamentos, incluindo o tratamento de exceções para recurso não encontrado e recurso em uso.

---

## 1. Criação da Branch no Git

No repositório remoto (GitHub), crie uma branch para implementar a nova issue. Em seguida, no ambiente local, execute:

```bash
git fetch origin
git checkout 8-8-implementar-endpoints-de-lançamentos
```

Esses comandos trazem a branch `8-8-implementar-endpoints-de-lançamentos` do repositório remoto para o ambiente local.

---

## 2. Atualize o LancamentoService

Abra o arquivo `LancamentoService` e adicione (ou atualize) os métodos para gerenciar o lançamento. Em especial:

### a) Método para deletar lançamento

Remova o `@Transactional` deste método para garantir que o tratamento de exceção funcione corretamente:

```java
public void deletarLancamento(Long codigo) {
    if (!lancamentoRepository.existsById(codigo)) {
        throw new RecursoNaoEncontradoException("Lançamento não encontrado");
    }
    try {
        lancamentoRepository.deleteById(codigo);
    } catch (DataIntegrityViolationException ex) {
        throw new RecursoEmUsoException("Lançamento em uso e não pode ser removido");
    }
}
```

---

## 3. Criação do Controller e Integração com o Service

Implemente (ou ajuste) o controller que expõe os endpoints e delega a lógica para o `LancamentoService`. Segue um exemplo simplificado:

```java
package com.exemplo.controller;

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
```

---

## 4. Testando a API

Utilize ferramentas como Insomnia, Postman ou cURL para testar os endpoints:

- **Listar todos os lançamentos:**
  - **GET** `http://localhost:8080/lancamentos`

- **Buscar lançamento por código:**
  - **GET** `http://localhost:8080/lancamentos/1`

- **Criar um novo lançamento:**
  - **POST** `http://localhost:8080/lancamentos`  
  - **Corpo (JSON):**
    ```json
    {
		"descricao": "Bahamas",
		"valor": 100.32,
		"dataVencimento": "2017-02-10",
		"dataPagamento": "2017-02-10",
		"observacao": null,
		"tipo": "DESPESA",
		"categoria": {
			"codigo": 2
		},
		"pessoa": {
			"codigo": 2
		}
	}
    ```

- **Atualizar um lançamento:**
  - **PUT** `http://localhost:8080/lancamentos/1`  
  - **Corpo (JSON):**
    ```json
    {
		"descricao": "Bahamas",
		"valor": 110.32,
		"dataVencimento": "2017-02-10",
		"dataPagamento": "2017-02-10",
		"observacao": null,
		"tipo": "DESPESA",
		"categoria": {
			"codigo": 2
		},
		"pessoa": {
			"codigo": 2
		}
	}
    ```

- **Deletar um lançamento:**
  - **DELETE** `http://localhost:8080/lancamentos/1`

---

## 5. Commit, Push e Pull Request

Após validar que a API está funcionando conforme esperado:

1. Faça o commit das alterações:
   ```bash
   git add .
   git commit -m "8-8-implementar-endpoints-de-lançamentos"
   ```
2. Envie as alterações para o repositório remoto:
   ```bash
   git push
   ```
3. Abra um Pull Request (PR) na branch de destino, seguindo as diretrizes do projeto para revisão e integração.

---

## 6. Sincronize a Branch Main no Ambiente Local

Após a integração, atualize sua branch main:

```bash
git checkout main
git pull
```

---