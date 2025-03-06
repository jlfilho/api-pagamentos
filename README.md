# Tutorial: 8.1. Implementar paginação, resumo de lançamento e filtros por parâmetro

---

## 1. Criação da Branch no Git

No repositório remoto (GitHub), crie uma branch para implementar a nova issue. Em seguida, no ambiente local, execute:

```bash
git fetch origin
git checkout 22-81-implementar-paginação-resumo-de-lançamento-e-filtros-por-parâmetro
```

Esses comandos trazem a branch `22-81-implementar-paginação-resumo-de-lançamento-e-filtros-por-parâmetros` do repositório remoto para o ambiente local.

---

## 2. Crie um DTO com os parâmetros do filtro desejado.

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

---


## 3. Crie a Query para pesquisar laçamentos usando filtros e paginação 

```java
@Query("SELECT l FROM Lancamento l " +
                     "WHERE (:descricao IS NULL OR lower(l.descricao) LIKE lower(concat('%', :descricao, '%'))) " +
                     "AND (:dataVencimentoDe IS NULL OR l.dataVencimento >= :dataVencimentoDe) " +
                     "AND (:dataVencimentoAte IS NULL OR l.dataVencimento <= :dataVencimentoAte)")
       Page<Lancamento> filtrar(@Param("descricao") String descricao,
                     @Param("dataVencimentoDe") LocalDate dataVencimentoDe,
                     @Param("dataVencimentoAte") LocalDate dataVencimentoAte,
                     Pageable pageable);
```

---

## 4. Crie em LancamentoService o método para pesquisar com paginação e filtro 

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

## 5. Crie em LancamentoController o método para pesquisar com paginação e filtro 

```java
    @GetMapping
    public ResponseEntity<Page<LancamentoDTO>> pesquisar(LancamentoFilterDTO lancamentoFilter, Pageable pageable) {
        Page<LancamentoDTO> lancamentos = lancamentoService.pesquisar(lancamentoFilter, pageable);
        return ResponseEntity.ok(lancamentos);
    }
```

Remova o método `listarLancamentos`. 


## 6. Testando a API

Utilize ferramentas como Insomnia, Postman ou cURL para testar o endpoint com filtros e paginação:

- **Listar todos os lançamentos:**
  - **GET** `http://localhost:8080/lancamentos`
  
- **Listar todos os lançamentos com filtro pelo parâmetro descriao:**
  - **GET** `http://localhost:8080/lancamentos?descricao=bahamas`

- **Listar todos os lançamentos com filtro por datas de vencimento:**
  - **GET** `http://localhost:8080/lancamentos?dataVencimentoDe=2017-02-01&dataVencimentoAte=2017-04-30`

- **Listar lançamentos por páginas:**
  - **GET** `http://localhost:8080/lancamentos?page=0&size=3`


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