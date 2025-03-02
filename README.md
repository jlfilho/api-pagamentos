# Tutorial: Criação do repositório e serviço para Pessoas

A seguir, um passo a passo detalhado para implementar o repositório e o serviço da entidade Pessoa, utilizando uma abordagem baseada em DTO (Data Transfer Object) para desacoplar a camada de persistência da camada de apresentação. O tutorial inclui desde a criação da branch para a nova feature até a abertura do pull request para a branch main.

---

## 1. Criação da Branch no Git

No GitHub remoto: Crie a branch para implementar a nova issue.
No ambiente local: Execute os seguintes comandos para buscar a branch e mudar para ela:
```bash
git fetch origin
git checkout 5-5-criação-do-repositório-e-serviço-para-pessoas
```

*Esses comandos criam uma branch chamada `5-5-criação-do-repositório-e-serviço-para-pessoas` no repositório remoto e as sincroniza com o local.*

---

## 2. Implementando o Repositório (PessoaRepository)

Crie uma interface que estenda o `JpaRepository` para a entidade `Pessoa`. Isso facilitará as operações CRUD sem a necessidade de implementação manual.

```java
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
    // Se necessário, adicione métodos de consulta customizados aqui.
}
```

*O repositório permite interagir com a tabela `pessoa` do banco de dados.*

---

## 3. Criação dos DTOs

Utilizar DTOs ajuda a expor somente os dados necessários, evitando o acoplamento direto com a entidade. Crie, por exemplo, o `PessoaDTO` e, se necessário, o `EnderecoDTO`.

### PessoaDTO

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PessoaDTO {
    private Long codigo;
    private String nome;
    private Boolean ativo;
    private EnderecoDTO endereco;
}
```

### EnderecoDTO

*Caso a classe `Endereco` possua atributos como logradouro, cidade, etc., crie um DTO correspondente:*

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoDTO {
    private String logradouro;
    private String cidade;
    private String estado;
    private String cep;
}
```

---

## 4. Implementando o Serviço (PessoaService)

Crie uma classe de serviço anotada com `@Service` para encapsular a lógica de negócio da entidade Pessoa. Essa camada usará o repositório e fará a conversão entre a entidade e o DTO.

```java
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PessoaService {

    private final PessoaRepository pessoaRepository;

    public PessoaService(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    // Método para converter Pessoa em PessoaDTO
    private PessoaDTO toDTO(Pessoa pessoa) {
        PessoaDTO dto = new PessoaDTO();
        dto.setCodigo(pessoa.getCodigo());
        dto.setNome(pessoa.getNome());
        dto.setAtivo(pessoa.getAtivo());
        if (pessoa.getEndereco() != null) {
            EnderecoDTO enderecoDTO = new EnderecoDTO();
            enderecoDTO.setLogradouro(pessoa.getEndereco().getLogradouro());
            enderecoDTO.setCidade(pessoa.getEndereco().getCidade());
            enderecoDTO.setEstado(pessoa.getEndereco().getEstado());
            enderecoDTO.setCep(pessoa.getEndereco().getCep());
            dto.setEndereco(enderecoDTO);
        }
        return dto;
    }

    // Método para converter PessoaDTO em Pessoa
    private Pessoa toEntity(PessoaDTO dto) {
        Pessoa pessoa = new Pessoa();
        pessoa.setCodigo(dto.getCodigo());
        pessoa.setNome(dto.getNome());
        pessoa.setAtivo(dto.getAtivo());
        if (dto.getEndereco() != null) {
            Endereco endereco = new Endereco();
            endereco.setLogradouro(dto.getEndereco().getLogradouro());
            endereco.setCidade(dto.getEndereco().getCidade());
            endereco.setEstado(dto.getEndereco().getEstado());
            endereco.setCep(dto.getEndereco().getCep());
            pessoa.setEndereco(endereco);
        }
        return pessoa;
    }

    @Transactional
    public PessoaDTO criarPessoa(PessoaDTO pessoaDTO) {
        Pessoa pessoa = toEntity(pessoaDTO);
        Pessoa savedPessoa = pessoaRepository.save(pessoa);
        return toDTO(savedPessoa);
    }

    public PessoaDTO buscarPessoaPorCodigo(Long codigo) {
        Pessoa pessoa = pessoaRepository.findById(codigo)
                .orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));
        return toDTO(pessoa);
    }

    public List<PessoaDTO> listarPessoas() {
        return pessoaRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PessoaDTO atualizarPessoa(Long codigo, PessoaDTO pessoaDTO) {
        Pessoa pessoaExistente = pessoaRepository.findById(codigo)
                .orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));

        pessoaExistente.setNome(pessoaDTO.getNome());
        pessoaExistente.setAtivo(pessoaDTO.getAtivo());
        if (pessoaDTO.getEndereco() != null) {
            Endereco endereco = new Endereco();
            endereco.setLogradouro(pessoaDTO.getEndereco().getLogradouro());
            endereco.setCidade(pessoaDTO.getEndereco().getCidade());
            endereco.setEstado(pessoaDTO.getEndereco().getEstado());
            endereco.setCep(pessoaDTO.getEndereco().getCep());
            pessoaExistente.setEndereco(endereco);
        }

        Pessoa updatedPessoa = pessoaRepository.save(pessoaExistente);
        return toDTO(updatedPessoa);
    }

    @Transactional
    public void deletarPessoa(Long codigo) {
        if (!pessoaRepository.existsById(codigo)) {
            throw new RecursoNaoEncontradoException("Pessoa não encontrada");
        }
        try {
            pessoaRepository.deleteById(codigo);
        } catch (DataIntegrityViolationException e) {
            throw new RecursoEmUsoException("Pessoa em uso e não pode ser removida");
        }

    }
}
```

*Nesse serviço, métodos básicos de CRUD foram implementados, com conversão entre Pessoa e PessoaDTO para manter o acoplamento baixo entre as camadas.*

---

## 5. Realize o commit, push e abra um Pull Request para essa issue
 -  Após validar que os dados foram carregados corretamente, efetue o commit das alterações e faça o push para o repositório remoto.
   ```
   git add .
   git commit -m "5-5-criação-do-repositório-e-serviço-para-pessoas"
   git push 
   ```

- Em seguida, abra um Pull Request (PR) na branch de destino, descrevendo as alterações realizadas. Certifique-se de que o PR esteja de acordo com as diretrizes do projeto para revisão e integração.

## 6. Sincronize a branch main do diretório local

- No diretório local, retorne para a branch main e atualize com o diretório remoto.
```
git checkout main
git pull
```