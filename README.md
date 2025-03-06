# Tutorial: 9. Validações 

Este tutorial demonstra como implementar validações na sua aplicação, utilizando Bean Validation e as boas práticas de desenvolvimento.

---

## 1. Criação da Branch no Git

No repositório remoto (GitHub), crie uma branch para implementar a nova issue. Em seguida, no ambiente local, execute os comandos:

```bash
git fetch origin
git checkout 9-9-validações-e-tratamento-de-erros
```

> **Observação:** Utilize o mesmo nome da branch tanto no repositório remoto quanto no local. Neste exemplo, usamos um nome sem acentos para evitar problemas de encoding.

---

## 2. Adicionando as Validações em Entidades e DTOs

### 2.1 Atualizando a entidade **Endereco**

Remova o `EnderecoDTO` (caso exista) e atualize a entidade `Endereco` com as validações necessárias:

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Endereco {
   
    @NotBlank(message = "O logradouro é obrigatório")
    @Size(min = 5, max = 255, message = "O logradouro deve ter entre 5 e 255 caracteres")
    private String logradouro;

    @NotBlank(message = "O número é obrigatório")
    @Size(max = 10, message = "O número não pode exceder 10 caracteres")
    private String numero;

    @Size(max = 255, message = "O complemento não pode exceder 255 caracteres")
    private String complemento;

    @NotBlank(message = "O bairro é obrigatório")
    @Size(min = 3, max = 100, message = "O bairro deve ter entre 3 e 100 caracteres")
    private String bairro;

    @NotBlank(message = "O CEP é obrigatório")
    @Pattern(regexp = "\\d{5}-\\d{3}", message = "O CEP deve seguir o formato 12345-678")
    private String cep;

    @NotBlank(message = "A cidade é obrigatória")
    @Size(min = 3, max = 100, message = "A cidade deve ter entre 3 e 100 caracteres")
    private String cidade;

    @NotBlank(message = "O estado é obrigatório")
    @Size(min = 2, max = 2, message = "O estado deve ter 2 caracteres (sigla)")
    private String estado;
}
```

### 2.2 Atualizando a entidade **Pessoa** e o DTO **PessoaDTO**

Atualize a entidade `Pessoa` para incluir a validação do atributo `endereco` em cascata, utilizando a anotação `@Valid`:

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pessoa")
public class Pessoa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;
    
    @NotNull
    @Size(min = 3, max = 50)
    private String nome;
    
    @NotNull
    private Boolean ativo;

    @NotNull(message = "O endereço é obrigatório")
    @Valid
    @Embedded
    private Endereco endereco;
}
```

O DTO correspondente também deve refletir as mesmas validações:

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PessoaDTO {
    private Long codigo;
    
    @NotNull
    @Size(min = 3, max = 50)
    private String nome;
    
    @NotNull
    private Boolean ativo;

    @NotNull(message = "O endereço é obrigatório")
    @Valid
    @Embedded
    private Endereco endereco;
}
```

### 2.3 Atualizando a entidade **Lancamento** e o DTO **LancamentoDTO**

Na entidade `Lancamento`, inclua validações nos campos obrigatórios:

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lancamento")
public class Lancamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;

    @NotBlank(message = "A descrição é obrigatória")
    @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
    private String descricao;

    @NotNull(message = "O valor é obrigatório")
    @Positive(message = "O valor deve ser positivo")
    private BigDecimal valor;

    @NotNull(message = "A data de vencimento é obrigatória")
    private LocalDate dataVencimento;

    private LocalDate dataPagamento;

    @Size(max = 500, message = "A observação deve ter no máximo 500 caracteres")
    private String observacao;

    @NotNull(message = "O tipo de lançamento é obrigatório")
    @Enumerated(EnumType.STRING)
    private TipoLancamento tipo;

    @NotNull(message = "A categoria é obrigatória")
    @ManyToOne
    @JoinColumn(name = "categoria_codigo")
    private Categoria categoria;

    @NotNull(message = "A pessoa é obrigatória")
    @ManyToOne
    @JoinColumn(name = "pessoa_codigo")
    private Pessoa pessoa;
}
```

O DTO correspondente:

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LancamentoDTO {
    private Long codigo;
    
    @NotBlank(message = "A descrição é obrigatória")
    @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
    private String descricao;

    @NotNull(message = "O valor é obrigatório")
    @Positive(message = "O valor deve ser positivo")
    private BigDecimal valor;

    @NotNull(message = "A data de vencimento é obrigatória")
    private LocalDate dataVencimento;

    private LocalDate dataPagamento;

    @Size(max = 500, message = "A observação deve ter no máximo 500 caracteres")
    private String observacao;

    @NotNull(message = "O tipo de lançamento é obrigatório")
    private TipoLancamento tipo;

    @NotNull(message = "A categoria é obrigatória")
    private Categoria categoria;

    @NotNull(message = "A pessoa é obrigatória")
    private Pessoa pessoa;
}
```

---

## 3. Atualizando os Métodos toDTO e toEntity nos Services

### 3.1 Em PessoaService

Adapte os métodos de conversão entre `Pessoa` e `PessoaDTO`:

```java
// Converte Pessoa para PessoaDTO
private PessoaDTO toDTO(Pessoa pessoa) {
    PessoaDTO dto = new PessoaDTO();
    dto.setCodigo(pessoa.getCodigo());
    dto.setNome(pessoa.getNome());
    dto.setAtivo(pessoa.getAtivo());
    dto.setEndereco(pessoa.getEndereco());
    return dto;
}

// Converte PessoaDTO para Pessoa
private Pessoa toEntity(PessoaDTO dto) {
    Pessoa pessoa = new Pessoa();
    pessoa.setCodigo(dto.getCodigo());
    pessoa.setNome(dto.getNome());
    pessoa.setAtivo(dto.getAtivo());
    pessoa.setEndereco(dto.getEndereco());
    return pessoa;
}
```

### 3.2 Em LancamentoService

Adapte os métodos de conversão entre `Lancamento` e `LancamentoDTO`:

```java
// Converte Lancamento para LancamentoDTO
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

// Converte LancamentoDTO para Lancamento
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
```

---

## 4. Atualizando os Endpoints nos Controllers

### 4.1 PessoaController

Atualize os métodos POST e PUT para incluir a validação:

```java
@PutMapping("/{codigo}")
public ResponseEntity<PessoaDTO> atualizarPessoa(@Valid @PathVariable Long codigo, @RequestBody PessoaDTO pessoa) {
    PessoaDTO atualizada = pessoaService.atualizarPessoa(codigo, pessoa);
    return ResponseEntity.ok(atualizada);
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
```

### 4.2 LancamentoController

Atualize os endpoints POST e PUT:

```java
// POST /lancamentos
@PostMapping
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
public ResponseEntity<LancamentoDTO> atualizarLancamento(@Valid @PathVariable Long codigo,
        @RequestBody LancamentoDTO lancamentoDTO) {
    LancamentoDTO lancamentoAtualizado = lancamentoService.atualizarLancamento(codigo, lancamentoDTO);
    return ResponseEntity.ok(lancamentoAtualizado);
}
```

---

## 5. Testando os Endpoints

Utilize ferramentas como Insomnia, Postman ou cURL para testar os endpoints:
- Envie requisições com dados válidos e inválidos.
- Verifique se as regras de validação estão funcionando conforme esperado e se os erros são tratados corretamente.

---

## 6. Commit, Push e Pull Request

Após confirmar que a API está funcionando conforme esperado:

1. Faça o commit das alterações:
   ```bash
   git add .
   git commit -m "9-9-validações-e-tratamento-de-erros"
   ```
2. Envie as alterações para o repositório remoto:
   ```bash
   git push
   ```
3. Abra um Pull Request (PR) na branch de destino, seguindo as diretrizes do projeto para revisão e integração.

---

## 7. Sincronizando a Branch Main no Ambiente Local

Após a integração, atualize sua branch main:

```bash
git checkout main
git pull
```