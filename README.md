# Tutorial: Criação do repositório e serviço para Lançamento

---

### 1. Criação da Branch no Git

No repositório remoto, crie a branch para implementar a nova funcionalidade. No ambiente local, execute os seguintes comandos para buscar e mudar para a branch:

```bash
git fetch origin
git checkout -b 7-7-criação-do-repositório-e-serviço-para-lançamentos origin/main
```

Esses comandos criam e sincronizam a branch **7-7-criação-do-repositório-e-serviço-para-lançamentos** com o repositório remoto.

---

### 2. Implementando o Repositório (LancamentoRepository)

Crie uma interface que estenda o `JpaRepository` para a entidade `Lancamento`. Dessa forma, você terá acesso às operações CRUD sem a necessidade de implementá-las manualmente.

```java
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uea.edu.dsw.api_pagamentos.model.Lancamento;

@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {
    // Adicione métodos customizados, se necessário.
}
```

Esse repositório permitirá interagir com a tabela **lancamento** no banco de dados.

---

### 3. Criação dos DTOs

Utilizar DTOs ajuda a expor somente os dados necessários, evitando o acoplamento direto com a entidade.

#### LancamentoDTO

Crie um DTO para a entidade `Lancamento`, incluindo os campos e também as referências às entidades relacionadas (Categoria e Pessoa), podendo ser representadas por DTOs simplificados:

```java
import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uea.edu.dsw.api_pagamentos.model.Categoria;
import uea.edu.dsw.api_pagamentos.model.TipoLancamento;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LancamentoDTO {
    private Long codigo;
    private String descricao;
    private BigDecimal valor;
    private LocalDate dataVencimento;
    private LocalDate dataPagamento;
    private String observacao;
    private TipoLancamento tipo;
    private CategoriaDTO categoria;
    private PessoaDTO pessoa;
}
```

#### PessoaDTO (Exemplo)

Se já existir o DTO para Pessoa, você pode utilizá-lo diretamente. Caso contrário, segue um exemplo básico:

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PessoaDTO {
    private Long codigo;
    private String nome;
}
```

---

### 4. Implementando o Serviço (LancamentoService)

Crie uma classe de serviço anotada com `@Service` para encapsular a lógica de negócio da entidade `Lancamento`. Nessa camada, serão realizadas as conversões entre a entidade e o DTO e implementadas as operações de CRUD.

```java
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uea.edu.dsw.api_pagamentos.dto.LancamentoDTO;
import uea.edu.dsw.api_pagamentos.dto.PessoaDTO;
import uea.edu.dsw.api_pagamentos.model.Categoria;
import uea.edu.dsw.api_pagamentos.model.Lancamento;
import uea.edu.dsw.api_pagamentos.model.Pessoa;
import uea.edu.dsw.api_pagamentos.repository.LancamentoRepository;
import uea.edu.dsw.api_pagamentos.service.exception.RecursoEmUsoException;
import uea.edu.dsw.api_pagamentos.service.exception.RecursoNaoEncontradoException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LancamentoService {

    private final LancamentoRepository lancamentoRepository;

    public LancamentoService(LancamentoRepository lancamentoRepository) {
        this.lancamentoRepository = lancamentoRepository;
    }

    // Método para converter Lancamento em LancamentoDTO
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
        if (lancamento.getPessoa() != null) {
            PessoaDTO pessoaDTO = new PessoaDTO();
            pessoaDTO.setCodigo(lancamento.getPessoa().getCodigo());
            pessoaDTO.setNome(lancamento.getPessoa().getNome());
            dto.setPessoa(pessoaDTO);
        }
        return dto;
    }

    // Método para converter LancamentoDTO em Lancamento
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
        if (dto.getPessoa() != null) {
            Pessoa pessoa = new Pessoa();
            pessoa.setCodigo(dto.getPessoa().getCodigo());
            pessoa.setNome(dto.getPessoa().getNome());
            lancamento.setPessoa(pessoa);
        }
        return lancamento;
    }

    @Transactional
    public LancamentoDTO criarLancamento(LancamentoDTO lancamentoDTO) {
        Lancamento lancamento = toEntity(lancamentoDTO);
        Lancamento savedLancamento = lancamentoRepository.save(lancamento);
        return toDTO(savedLancamento);
    }

    public LancamentoDTO buscarLancamentoPorCodigo(Long codigo) {
        Lancamento lancamento = lancamentoRepository.findById(codigo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lançamento não encontrado"));
        return toDTO(lancamento);
    }

    public List<LancamentoDTO> listarLancamentos() {
        return lancamentoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public LancamentoDTO atualizarLancamento(Long codigo, LancamentoDTO lancamentoDTO) {
        Lancamento lancamentoExistente = lancamentoRepository.findById(codigo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lançamento não encontrado"));

        lancamentoExistente.setDescricao(lancamentoDTO.getDescricao());
        lancamentoExistente.setValor(lancamentoDTO.getValor());
        lancamentoExistente.setDataVencimento(lancamentoDTO.getDataVencimento());
        lancamentoExistente.setDataPagamento(lancamentoDTO.getDataPagamento());
        lancamentoExistente.setObservacao(lancamentoDTO.getObservacao());
        lancamentoExistente.setTipo(lancamentoDTO.getTipo());

        if (lancamentoDTO.getCategoria() != null) {
            Categoria categoria = new Categoria();
            categoria.setCodigo(lancamentoDTO.getCategoria().getCodigo());
            categoria.setNome(lancamentoDTO.getCategoria().getNome());
            lancamentoExistente.setCategoria(categoria);
        }
        if (lancamentoDTO.getPessoa() != null) {
            Pessoa pessoa = new Pessoa();
            pessoa.setCodigo(lancamentoDTO.getPessoa().getCodigo());
            pessoa.setNome(lancamentoDTO.getPessoa().getNome());
            lancamentoExistente.setPessoa(pessoa);
        }

        Lancamento LancamentoAtualizado = lancamentoRepository.save(lancamentoExistente);
        return toDTO(LancamentoAtualizado);
    }

    @Transactional
    public void deletarLancamento(Long codigo) {
        if (!lancamentoRepository.existsById(codigo)) {
            throw new RecursoNaoEncontradoException("Lançamento não encontrado");
        }
        try {
            lancamentoRepository.deleteById(codigo);
        } catch (Exception e) {
            throw new RecursoEmUsoException("Lançamento em uso e não pode ser removido");
        }
    }
}
```

Nesse serviço, os métodos básicos de CRUD foram implementados, com conversão entre `Lancamento` e `LancamentoDTO` para manter o acoplamento baixo entre as camadas.

---

### 5. Commit, Push e Pull Request

Após validar que a aplicação está funcionando conforme o esperado, efetue o commit das alterações e faça o push para o repositório remoto:

```bash
git add .
git commit -m "7-7-criação-do-repositório-e-serviço-para-lançamentos"
git push origin 7-7-criação-do-repositório-e-serviço-para-lançamentos
```

Em seguida, abra um Pull Request (PR) na branch de destino (geralmente a branch **main**), descrevendo detalhadamente as alterações realizadas e seguindo as diretrizes do projeto para revisão e integração.

---

### 6. Sincronizando a Branch Main no Ambiente Local

Após a aprovação e merge do PR, sincronize sua branch main localmente:

```bash
git checkout main
git pull origin main
```

---