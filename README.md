# Tutorial: 8.1. Implementar Paginação, Resumo de Lançamento e Filtros por Parâmetro

## 1. Criação da Branch no Git

No repositório remoto (GitHub), crie uma branch para implementar a nova issue. Em seguida, no ambiente local, execute:

```bash
git fetch origin
git checkout 22-81-implementar-paginacao-resumo-de-lancamento-e-filtros-por-parametro
```

> **Observação:** Certifique-se de utilizar o mesmo nome da branch tanto no repositório remoto quanto no local. Neste exemplo, usei um nome sem acentos para evitar problemas com encoding.

---

## 2. Criação dos DTOs

### DTO de Filtro: LancamentoFilterDTO

Crie um DTO para receber os parâmetros de filtro:

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LancamentoFilterDTO {
    private String descricao;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataVencimentoDe;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataVencimentoAte;
}
```

### DTO de Resumo: ResumoLancamentoDTO

Crie um DTO para retornar o resumo dos lançamentos:

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumoLancamentoDTO {
    private Long codigo;
    private String descricao;
    private LocalDate dataVencimento;
    private LocalDate dataPagamento;
    private BigDecimal valor;
    private TipoLancamento tipo;
    private String categoria;
    private String pessoa;
}
```

> **Dica:** Certifique-se de que as classes e seus construtores estão no pacote correto (por exemplo, `uea.edu.dsw.api_pagamentos.dto`) para que possam ser referenciadas sem problemas nas queries JPQL.

---

## 3. Criação da Query no Repository

No repositório, crie um método com a consulta JPQL para filtrar os lançamentos com base nos parâmetros e com suporte à paginação:

```java
@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    @Query("SELECT l FROM Lancamento l " +
           "WHERE (:descricao IS NULL OR lower(l.descricao) LIKE lower(concat('%', :descricao, '%'))) " +
           "AND (:dataVencimentoDe IS NULL OR l.dataVencimento >= :dataVencimentoDe) " +
           "AND (:dataVencimentoAte IS NULL OR l.dataVencimento <= :dataVencimentoAte)")
    Page<Lancamento> filtrar(@Param("descricao") String descricao,
                             @Param("dataVencimentoDe") LocalDate dataVencimentoDe,
                             @Param("dataVencimentoAte") LocalDate dataVencimentoAte,
                             Pageable pageable);
}
```

> **Observação:** Os parâmetros são opcionais. Se algum deles for `null`, a condição correspondente será ignorada na consulta.

---

## 4. Método para Converter Lancamento em ResumoLancamentoDTO

No serviço, crie um método para converter a entidade **Lancamento** em **ResumoLancamentoDTO**:

```java
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
```

---

## 5. Método de Serviço para Pesquisar com Paginação e Filtro

Crie um método em **LancamentoService** para pesquisar os lançamentos usando os filtros e a paginação. Esse método retorna uma página de **LancamentoDTO** (conversão realizada pelo método `toDTO` já existente):

```java
@Transactional(readOnly = true)
public Page<LancamentoDTO> pesquisar(LancamentoFilterDTO lancamentoFilter, Pageable pageable) {
    Page<Lancamento> lancamentosPage = lancamentoRepository.filtrar(
            lancamentoFilter.getDescricao(),
            lancamentoFilter.getDataVencimentoDe(),
            lancamentoFilter.getDataVencimentoAte(),
            pageable);
    return lancamentosPage.map(this::toDTO);
}
```

---

## 6. Método de Serviço para Resumir Lançamentos com Paginação e Filtro

Crie o método para retornar o resumo dos lançamentos:

```java
@Transactional(readOnly = true)
public Page<ResumoLancamentoDTO> resumir(LancamentoFilterDTO lancamentoFilter, Pageable pageable) {
    Page<Lancamento> lancamentosPage = lancamentoRepository.filtrar(
            lancamentoFilter.getDescricao(),
            lancamentoFilter.getDataVencimentoDe(),
            lancamentoFilter.getDataVencimentoAte(),
            pageable);
    return lancamentosPage.map(this::toResumoDTO);
}
```

---

## 7. Método de Pesquisa no Controller

No **LancamentoController**, remova o método antigo de listagem e crie um método para pesquisar lançamentos com paginação e filtros:

```java
// GET /lancamentos
@GetMapping
public ResponseEntity<Page<LancamentoDTO>> pesquisar(LancamentoFilterDTO lancamentoFilter, Pageable pageable) {
    Page<LancamentoDTO> lancamentos = lancamentoService.pesquisar(lancamentoFilter, pageable);
    return ResponseEntity.ok(lancamentos);
}
```

---

## 8. Método de Resumo no Controller

Crie o endpoint para resumir os lançamentos com filtros e paginação:

```java
// GET /lancamentos/resumo
@GetMapping("/resumo")
public ResponseEntity<Page<ResumoLancamentoDTO>> resumir(LancamentoFilterDTO lancamentoFilter, Pageable pageable) {
    Page<ResumoLancamentoDTO> lancamentos = lancamentoService.resumir(lancamentoFilter, pageable);
    return ResponseEntity.ok(lancamentos);
}
```

---

## 9. Testando a API

Utilize ferramentas como Insomnia, Postman ou cURL para testar os endpoints:

### Endpoints para Lançamentos

- **Listar todos os lançamentos:**  
  **GET** `http://localhost:8080/lancamentos`

- **Filtrar por descrição:**  
  **GET** `http://localhost:8080/lancamentos?descricao=bahamas`

- **Filtrar por datas de vencimento:**  
  **GET** `http://localhost:8080/lancamentos?dataVencimentoDe=2017-02-01&dataVencimentoAte=2017-04-30`

- **Paginação e ordenação:**  
  **GET** `http://localhost:8080/lancamentos?page=0&size=3&sort=dataVencimento,asc`

### Endpoints para Resumo de Lançamentos

- **Listar resumo de todos os lançamentos:**  
  **GET** `http://localhost:8080/lancamentos/resumo`

- **Filtrar resumo por descrição:**  
  **GET** `http://localhost:8080/lancamentos/resumo?descricao=bahamas`

- **Filtrar resumo por datas de vencimento:**  
  **GET** `http://localhost:8080/lancamentos/resumo?dataVencimentoDe=2017-02-01&dataVencimentoAte=2017-04-30`

- **Paginação e ordenação:**  
  **GET** `http://localhost:8080/lancamentos/resumo?page=0&size=3&sort=dataVencimento,asc`

> **Dica:** Ao testar no Insomnia ou Postman, adicione os parâmetros de página, tamanho e ordenação na aba "Query" da requisição.

---

## 10. Commit, Push e Pull Request

Após confirmar que a API está funcionando conforme esperado:

1. Faça o commit das alterações:
   ```bash
   git add .
   git commit -m "22-81-implementar-paginacao-resumo-de-lancamento-e-filtros-por-parametro"
   ```
2. Envie as alterações para o repositório remoto:
   ```bash
   git push
   ```
3. Abra um Pull Request (PR) na branch de destino, seguindo as diretrizes do projeto para revisão e integração.

---

## 11. Sincronize a Branch Main no Ambiente Local

Após a integração, atualize sua branch main:

```bash
git checkout main
git pull
```

---