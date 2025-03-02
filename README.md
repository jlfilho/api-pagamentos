# Tutorial: Implementação de Exceções Customizadas e Exception Handler

## 1. Criação da Branch no Git

1. **No GitHub remoto:** Crie a branch para implementar a nova issue.
2. **No ambiente local:** Execute os seguintes comandos para buscar a branch e mudar para ela:

```bash
git fetch origin
git checkout 15-41-implementar-tratamento-de-exceção-customizada
```

---

## 2. Criação das Classes de Exceção Customizada

### a) Crie o subpacote `exception`

No pacote de _services_, crie um subpacote chamado `exception`.

### b) Classe `RecursoNaoEncontradoException`

Esta exceção estende `RuntimeException`, permitindo lançá-la sem obrigar o uso de try/catch explícito.

```java
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String message) {
        super(message);
    }

    public RecursoNaoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

> **Dica:** Utilizar `RuntimeException` evita a obrigatoriedade de tratamento explícito em cada método.

### c) Classe `RecursoEmUsoException`

Crie também a exceção para indicar que o recurso está em uso e não pode ser removido:

```java
public class RecursoEmUsoException extends RuntimeException {

    public RecursoEmUsoException(String message) {
        super(message);
    }

    public RecursoEmUsoException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

---

## 3. Lançamento das Exceções no Service

### a) Método para buscar recurso por código

Utilize o método `orElseThrow` para lançar a exceção caso o recurso não seja encontrado:

```java
public Categoria buscarPorCodigo(Long codigo) {
    return categoriaRepository.findById(codigo)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Recurso com ID " + codigo + " não encontrado."));
}
```

> **Observação:** Essa abordagem torna o código mais limpo ao evitar condicionais explícitas.

### b) Refatoração do método atualizar

```java
public Categoria atualizar(Long codigo, Categoria categoria) {
    Categoria categoriaExistente = categoriaRepository.findById(codigo)
        .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada!"));

    categoriaExistente.setNome(categoria.getNome());
    return categoriaRepository.save(categoriaExistente);
}
```

### c) Refatoração do método deletar

```java
public void deletar(Long codigo) {
    Categoria categoriaExistente = categoriaRepository.findById(codigo)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada!"));

    try {
        categoriaRepository.delete(categoriaExistente);
    } catch (DataIntegrityViolationException ex) {
        throw new RecursoEmUsoException("Categoria em uso e não pode ser removida.", ex);
    }
}
```

> **Observação:** Caso a operação de delete viole a integridade referencial, a exceção `DataIntegrityViolationException` é capturada e relançada como `RecursoEmUsoException`.

---

## 4. Criação do DTO para Resposta de Erro

Crie uma classe que represente a estrutura da resposta de erro enviada ao cliente, contendo status HTTP, mensagem e timestamp.

```java
@Data
public class ErrorResponse {
    private int status;
    private String message;
    private long timestamp;

    public ErrorResponse(int status, String message, long timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }
}
```

---

## 5. Implementação do Exception Handler

Crie uma classe anotada com `@RestControllerAdvice` (ou `@ControllerAdvice` para aplicações não REST) para interceptar as exceções e retornar respostas apropriadas.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleRecursoNaoEncontradoException(RecursoNaoEncontradoException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RecursoEmUsoException.class)
    public ResponseEntity<ErrorResponse> handleCategoriaEmUsoException(RecursoEmUsoException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            ex.getMessage(),
            System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
}
```

> **Dica:** Use o status 404 para recursos não encontrados e 409 para conflitos de integridade (categoria em uso).

---

## 6. Ajuste no Controller

No controller, ajuste o método de busca para utilizar o service sem o uso de `.get()`, deixando o tratamento de exceção para o handler:

```java
@GetMapping("/{codigo}")
public ResponseEntity<Categoria> buscarPorCodigo(@PathVariable Long codigo) {
    Categoria categoria = categoriaService.buscarPorCodigo(codigo);
    return ResponseEntity.ok(categoria);
}
```

---

## 7. Teste a Implementação

1. **Executando a Aplicação:**  
   Inicie a aplicação e faça requisições utilizando ferramentas como Postman, Insomnia ou o navegador.  
   Exemplos de requisições:  
   - `GET /categorias/{codigo}` para buscar uma categoria.  
   - `PUT /categorias/{codigo}` para atualizar uma categoria.
   - `DELETE /categorias/{codigo}` para remover uma categoria.

2. **Verificação da Resposta:**  
   - Se o recurso não existir, o Exception Handler deverá retornar uma resposta com status HTTP **404 Not Found** e o corpo definido no DTO `ErrorResponse`.  
   - Se tentar remover uma categoria em uso, o Exception Handler deverá retornar uma resposta com status HTTP **409 Conflict**.

---

Seguindo esses passos, você terá uma implementação robusta para tratar exceções customizadas na sua aplicação, facilitando a manutenção e garantindo respostas de erro claras e consistentes.

## 8. Realize o commit, push e abra um Pull Request para essa issue
 -  Após validar que os dados foram carregados corretamente, efetue o commit das alterações e faça o push para o repositório remoto.
   ```
   git add .
   git commit -m "15-41-implementar-tratamento-de-exceção-customizada"
   git push 
   ```

- Em seguida, abra um Pull Request (PR) na branch de destino, descrevendo as alterações realizadas. Certifique-se de que o PR esteja de acordo com as diretrizes do projeto para revisão e integração.

## 9. Sincronize a branch main do diretório local

- No diretório local, retorne para a branch main e atualize com o diretório remoto.
```
git checkout main
git pull
```

---

## 10. Considerações Finais

- **Localização do Exception Handler:** Certifique-se de que a classe anotada com `@RestControllerAdvice` esteja em um pacote que seja escaneado pelo Spring Boot.
- **Personalização:** É possível criar handlers adicionais para outras exceções e personalizar as respostas (ex.: adicionando códigos de erro ou links para documentação).
- **Consistência:** O uso de exceções customizadas e de um handler centralizado melhora a manutenção e a clareza na comunicação de erros com os clientes.
