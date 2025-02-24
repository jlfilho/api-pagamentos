# 4. Implementar endpoints de Categorias

A seguir, um passo a passo detalhado para implementar os endpoints de Categorias (GET, POST, PUT e DELETE), incluindo as validações e definição de status HTTP:

---

### 1. Criar o Controller

1. **Crie a classe CategoriaController**  
   - No pacote, por exemplo, `com.seuprojeto.controller`, crie uma classe chamada `CategoriaController`.

2. **Anote a classe**  
   - Utilize `@RestController` para indicar que os métodos retornam dados JSON.
   - Utilize `@RequestMapping("/categorias")` para definir a rota base dos endpoints.

---

### 2. Injetar o Serviço

1. **Injete o CategoriaService**  
   - Utilize a injeção de dependência (preferencialmente via construtor) para acessar a camada de negócio.

   ```java
    private final CategoriaService categoriaService;
    
    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }
    ```

---

### 3. Implementar GET /categorias

1. **Defina o método**  
   - Utilize `@GetMapping` sem parâmetros para retornar a lista de todas as categorias.

2. **Validação e status HTTP**  
   - Retorne a lista com status HTTP 200 (OK).
   - Caso a lista esteja vazia, você pode optar por retornar uma lista vazia ou o status 204 (No Content), conforme a necessidade do negócio.

**Exemplo:**
```java
    @GetMapping
    public ResponseEntity<List<Categoria>> listarTodas() {
        List<Categoria> categorias = categoriaService.listarTodas();
        if (categorias.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(categorias);
    }
```

---

### 4. Implementar GET /categorias/{codigo}

1. **Defina o método**  
   - Utilize `@GetMapping("/{codigo}")` e anote o parâmetro com `@PathVariable`.

**Exemplo:**
```java
@GetMapping("/{codigo}")
    public ResponseEntity<Categoria> buscarPorCodigo(@PathVariable Long codigo) {
        Categoria categoria = categoriaService.buscarPorCodigo(codigo).get();
        return ResponseEntity.ok(categoria);
    }
```

> *Observação:* Se for passado um código inexistente, haverá um erro `Internal Server Error`. Na próxima issue será criado a exceção `RecursoNaoEncontradoException` e, configurado um handler global para transformar essa exceção em um status HTTP 404.

---

### 5. Implementar POST /categorias

1. **Defina o método**  
   - Utilize `@PostMapping` para criar uma nova categoria.
   - Receba os dados com `@RequestBody` e utilize `@Valid` para disparar validações definidas na entidade.

2. **Validação e status HTTP**  
   - Após salvar a categoria, retorne o status HTTP 201 (Created).
   - Utilize o `ResponseEntity` para incluir o header `Location` (opcional) apontando para o novo recurso.

**Exemplo:**
```java
@PostMapping
public ResponseEntity<Categoria> criarCategoria(@Valid @RequestBody Categoria categoria) {
    Categoria novaCategoria = categoriaService.salvar(categoria);
    URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
             .path("/{codigo}")
             .buildAndExpand(novaCategoria.getCodigo())
             .toUri();
    return ResponseEntity.created(uri).body(novaCategoria);
}
```

---
### 6. Implementar PUT /categorias/{codigo}

1. **Criar o método de atualização:**  
   - Utilize a anotação `@PutMapping("/{codigo}")` para mapear a rota.
   - Receba o identificador (`@PathVariable Long codigo`) e o objeto da categoria atualizado (`@RequestBody @Valid Categoria categoria`).
2. **Chamar o serviço de atualização:**  
   - No método, invoque o serviço responsável pela atualização. Esse método no service deverá buscar a categoria existente, atualizar os campos necessários e salvar as alterações.
3. **Retornar o ResponseEntity:**  
   - Após a atualização, retorne um `ResponseEntity` com a categoria atualizada e status HTTP 200 (OK).

**Exemplo:**

```java
@PutMapping("/{codigo}")
public ResponseEntity<Categoria> atualizarCategoria(@PathVariable Long codigo,
    @Valid @RequestBody Categoria categoria) {
    Categoria categoriaAtualizada = categoriaService.atualizar(codigo, categoria);
    return ResponseEntity.ok(categoriaAtualizada);
}
```
---

### 7. Implementar DELETE /categorias/{codigo}

1. **Criar o método de exclusão:**  
   - Utilize a anotação `@DeleteMapping("/{codigo}")` para mapear a rota.
   - Receba o identificador da categoria via `@PathVariable`.
2. **Chamar o serviço de exclusão:**  
   - O método do service deve verificar se a categoria existe e, em seguida, realizar a exclusão.
3. **Retornar o ResponseEntity:**  
   - Após a exclusão, retorne um `ResponseEntity` com status 204 (No Content).

**Exemplo:**

```java
@DeleteMapping("/{codigo}")
public ResponseEntity<Void> deletarCategoria(@PathVariable Long codigo) {
    categoriaService.deletar(codigo);
    return ResponseEntity.noContent().build();
}
```

---

### 8. Testar os Endpoints

1. **Utilize ferramentas de testes**  
   - Teste os endpoints utilizando o Insomnia, Postman, cURL ou a própria interface do Swagger (se implementada).

2. **Valide os retornos e status HTTP**  
   - **GET /categorias:** Verifique se retorna status 200 e a lista de categorias.
   - **GET /categorias/{codigo}:**  
     - Se o recurso existir, retorne status 200 com os dados.
     - Se não existir, retorna erro 500 e a mensagem de erro.
   - **POST /categorias:**  
     - Com dados válidos, retorne status 201, o recurso criado e o header `Location`.
     - Com dados inválidos (violando as anotações de validação), retorne status 400 (Bad Request) com os erros.
   - **PUT /categorias/{codigo}:**  
     - Com dados válidos, retorne status 200, e a categoria alterada.
     - Com dados inválidos (violando as anotações de validação), retorne status 400 (Bad Request) com os erros.
     - Se não existir, retorna erro 500 e a mensagem de erro.
   - **DELETE /categorias/{codigo}:**  
     - Se o recurso existir, retorne status 204, no content.
     - Se tentar excluir uma categoria com lançamento, retorna erro 500 e a mensagem de erro. 
     - Se não existir, retorne erro 500 e a mensagem de erro.
---

### Realize o commit, push e abra um Pull Request para essa issue
 -  Após validar que os dados foram carregados corretamente, efetue o commit das alterações e faça o push para o repositório remoto.
   ```
   git add .
   git commit -m "4. Implementar endpoints de Categorias"
   git push 
   ```

- Em seguida, abra um Pull Request (PR) na branch de destino, descrevendo as alterações realizadas. Certifique-se de que o PR esteja de acordo com as diretrizes do projeto para revisão e integração.

### 5. Sincronize a branch main do diretório local

- No diretório local, retorne para a branch main e atualize com o diretório remoto.
```
git checkout main
git pull
```