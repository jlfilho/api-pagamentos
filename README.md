# Tutorial: Implementar Endpoints de Pessoas

Este tutorial orienta a implementação dos endpoints para gerenciamento de pessoas, incluindo o tratamento de exceções para recurso não encontrado, recurso em uso e tentativa de alteração de uma propriedade já definida.

---

## 1. Criação da Branch no Git

No repositório remoto (GitHub), crie uma branch para implementar a nova issue. Em seguida, no ambiente local, execute:

```bash
git fetch origin
git checkout 6-6-implementar-endpoints-de-pessoas
```

*Esses comandos trazem a branch `6-6-implementar-endpoints-de-pessoas` do repositório remoto para o ambiente local.*

---

## 2. Atualize o PessoaService

Abra o arquivo `PessoaService` e adicione (ou atualize) os métodos para gerenciar a pessoa. Em especial:

### a) Método para atualizar o status

Adicione um método que atualize o status de uma pessoa. Caso o status enviado seja igual ao já definido, lance uma exceção. Por exemplo:

```java
public PessoaDTO atualizarStatus(Long codigo, Boolean ativo) {
    Pessoa pessoaExistente = pessoaRepository.findById(codigo)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Pessoa não encontrada"));

    if (pessoaExistente.getAtivo() != null && pessoaExistente.getAtivo().equals(ativo)) {
        throw new IllegalArgumentException("O status 'ativo' já está definido como " + ativo + ".");
    }
    pessoaExistente.setAtivo(ativo);
    Pessoa pessoaAtualizada = pessoaRepository.save(pessoaExistente);
    return toDTO(pessoaAtualizada);
}
```

### b) Método para deletar pessoa

Remova o `@Transactional` deste método para garantir que o tratamento de exceção funcione corretamente:

```java
public void deletarPessoa(Long codigo) {
    if (!pessoaRepository.existsById(codigo)) {
        throw new RecursoNaoEncontradoException("Pessoa não encontrada");
    }
    try {
        pessoaRepository.deleteById(codigo);
    } catch (DataIntegrityViolationException ex) {
        throw new RecursoEmUsoException("Pessoa em uso e não pode ser removida");
    }
}
```

---

## 3. Atualize o DTO de Mensagem de Erro

Atualize o DTO para refletir o padrão de resposta de erro desejado, utilizando o tipo `Instant` para o timestamp:

```java
package uea.edu.dsw.api_pagamentos.service.exception;

import lombok.Data;
import java.time.Instant;

@Data
public class ErrorResponse {
    private int status;
    private String error;
    private Instant timestamp;

    public ErrorResponse(int status, String error, Instant timestamp) {
        this.status = status;
        this.error = error;
        this.timestamp = timestamp;
    }
}
```

---

## 4. Atualize o GlobalExceptionHandler

Configure o tratamento global de exceções, mapeando os status HTTP conforme o tipo de erro:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleRecursoNaoEncontradoException(RecursoNaoEncontradoException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                Instant.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RecursoEmUsoException.class)
    public ResponseEntity<ErrorResponse> handleRecursoEmUsoException(RecursoEmUsoException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                Instant.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                Instant.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
}
```

> **Observação:** Ajustamos o nome do método de tratamento da exceção para recurso em uso para manter a consistência.

---

## 5. Criação do Controller e Integração com o Service

Implemente (ou ajuste) o controller que expõe os endpoints e delega a lógica para o `PessoaService`. Segue um exemplo simplificado:

```java
package com.exemplo.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uea.edu.dsw.api_pagamentos.dto.PessoaDTO;
import uea.edu.dsw.api_pagamentos.service.PessoaService;

@RestController
@RequestMapping("/pessoas")
public class PessoaController {

    private final PessoaService pessoaService;

    public PessoaController(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
    }

    // GET /pessoas
    @GetMapping
    public List<PessoaDTO> listarPessoas() {
        return pessoaService.listarPessoas();
    }

    // GET /pessoas/{codigo}
    @GetMapping("/{codigo}")
    public PessoaDTO buscarPessoa(@PathVariable Long codigo) {
        return pessoaService.buscarPessoaPorCodigo(codigo);
    }

    // POST /pessoas
    @PostMapping
    public ResponseEntity<PessoaDTO> criarPessoa(@RequestBody PessoaDTO pessoaDTO) {
        PessoaDTO pessoaCriada = pessoaService.criarPessoa(pessoaDTO);
        return new ResponseEntity<>(pessoaCriada, HttpStatus.CREATED);
    }

    // PUT /pessoas/{codigo}
    @PutMapping("/{codigo}")
    public PessoaDTO atualizarPessoa(@PathVariable Long codigo, @RequestBody PessoaDTO pessoaDTO) {
        return pessoaService.atualizarPessoa(codigo, pessoaDTO);
    }

    // DELETE /pessoas/{codigo}
    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> deletarPessoa(@PathVariable Long codigo) {
        pessoaService.deletarPessoa(codigo);
        return ResponseEntity.noContent().build();
    }

    // PATCH /pessoas/{codigo}/ativo
    @PatchMapping("/{codigo}/ativo")
    public PessoaDTO atualizarStatus(@PathVariable Long codigo, @RequestBody Boolean ativo) {
        return pessoaService.atualizarStatus(codigo, ativo);
    }
}
```

> **Dica:** Embora o método de atualização de status utilize `PATCH` neste exemplo, você pode optar por `PUT` se preferir. Lembre-se de que o endpoint de atualização de status lança uma exceção se o status enviado já estiver definido.

---

## 6. Testando a API

Utilize ferramentas como [Insomnia](https://insomnia.rest/), Postman ou cURL para testar os endpoints:

- **Listar todas as pessoas:**  
  `GET http://localhost:8080/pessoas`

- **Buscar pessoa por código:**  
  `GET http://localhost:8080/pessoas/1`

- **Criar uma nova pessoa:**  
  `POST http://localhost:8080/pessoas`  
  Corpo (JSON):
  ```json
  {
    "nome": "João Silva",
    "ativo": true,
    "endereco": {
      "logradouro": "Rua do Abacaxi",
      "cidade": "Uberlândia",
      "estado": "MG",
      "cep": "38400-012"
    }
  }
  ```

- **Atualizar uma pessoa:**  
  `PUT http://localhost:8080/pessoas/1`  
  Corpo (JSON):
  ```json
  {
    "codigo": 1,
    "nome": "João Silva Cavalcante",
    "ativo": true,
    "endereco": {
      "logradouro": "Rua do Abacaxi",
      "cidade": "Itacoatiara",
      "estado": "AM",
      "cep": "69102-120"
    }
  }
  ```

- **Deletar uma pessoa:**  
  `DELETE http://localhost:8080/pessoas/1`

- **Atualizar o status de ativação:**  
  `PATCH http://localhost:8080/pessoas/1/ativo`  
  Corpo (JSON):
  ```json
  true
  ```

---

## 7. Commit, Push e Pull Request

Após validar que a API está funcionando conforme esperado:

1. Faça o commit das alterações:
   ```bash
   git add .
   git commit -m "6-6: Implementar endpoints de pessoas e tratamento de exceções"
   ```

2. Envie as alterações para o repositório remoto:
   ```bash
   git push
   ```

3. Abra um Pull Request (PR) na branch de destino, seguindo as diretrizes do projeto para revisão e integração.

---

## 8. Sincronize a Branch Main no Ambiente Local

Após a integração, atualize sua branch `main`:

```bash
git checkout main
git pull
```

---

## Considerações Finais

- **Tratamento de Erros:**  
  Caso o código informado não corresponda a uma pessoa existente, o método de busca lança a exceção `RecursoNaoEncontradoException` (tratada com status 404). Se ocorrer um erro de integridade (por exemplo, tentativa de deletar uma pessoa em uso), é lançada a `RecursoEmUsoException` (status 409). Para atualizações com status já definido, a exceção `IllegalArgumentException` é lançada (status 409).

- **Boas Práticas:**  
  Considere a utilização de DTOs para separar a camada de domínio da representação dos dados na API e o uso de anotações como `@Valid` para validação dos dados de entrada.

---