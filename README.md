# 3. Criação do repositório e serviço para Categorias

Segue um passo a passo detalhado para implementar o **CategoriaRepository** e o **CategoriaService**:

---

## 1. Implementando o CategoriaRepository

1. **Criar o pacote de repositórios**  
   - Se ainda não existir, crie um pacote, por exemplo: `com.seuprojeto.repository`.

2. **Criar a interface CategoriaRepository**  
   - No pacote criado, crie uma interface chamada `CategoriaRepository`.

3. **Estender a interface JpaRepository**  
   - Faça com que a interface estenda `JpaRepository<Categoria, Long>`, onde `Categoria` é a entidade e `Long` é o tipo do identificador (campo `codigo`).
   - Isso fornece métodos prontos para operações CRUD (salvar, buscar, deletar, etc.).

**Exemplo de código:**

```java
package com.seuprojeto.repository;

import com.seuprojeto.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    // Métodos customizados podem ser adicionados aqui, se necessário
}
```

---

## 2. Implementando o CategoriaService

1. **Criar o pacote de serviços**  
   - Se ainda não existir, crie um pacote, por exemplo: `com.seuprojeto.service`.

2. **Implementar a classe CategoriaService**  
   - Crie uma classe, por exemplo, `CategoriaService`.
   - Anote a classe com `@Service` para que o Spring a reconheça como um componente de serviço.

4. **Injetar o CategoriaRepository**  
   - Utilize injeção de dependência (preferencialmente via construtor) para ter acesso aos métodos do `CategoriaRepository` dentro do serviço.

5. **Implementar os métodos da classe**  
   - Utilize os métodos do repositório para realizar as operações de negócio, aplicando regras, validações ou lógica extra conforme necessário.

**Exemplo de implementação:**

```java
package com.seuprojeto.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import uea.edu.dsw.api_pagamentos.model.Categoria;
import uea.edu.dsw.api_pagamentos.repository.CategoriaRepository;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    // Injeção via construtor
    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    public Optional<Categoria> buscarPorCodigo(Long codigo) {
        return categoriaRepository.findById(codigo);
    }

    public Categoria salvar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public Categoria atualizar(Long codigo, Categoria categoria) {
        // Verifica se a categoria existe
        Optional<Categoria> categoriaExistente = categoriaRepository.findById(codigo);
        if (categoriaExistente.isPresent()) {
            // Atualize os atributos necessários
            Categoria catAtualizada = categoriaExistente.get();
            catAtualizada.setNome(categoria.getNome());
            return categoriaRepository.save(catAtualizada);
        } else {
            // Lógica de tratamento para categoria não encontrada
            throw new RuntimeException("Categoria não encontrada!");
        }
    }

    public void deletar(Long codigo) {
        categoriaRepository.deleteById(codigo);
    }
}
```

---

## 3. Realize o commit, push e abra um Pull Request para essa issue

 - Após validar que os dados foram carregados corretamente, efetue o commit das alterações e faça o push para o repositório remoto.

```
   git add .
   git commit -m "3. Criação do repositório e serviço para Categorias"
   git push 
```

 - Em seguida, abra um Pull Request (PR) na branch de destino, descrevendo as alterações realizadas. Certifique-se de que o PR esteja de acordo com as diretrizes do projeto para revisão e integração.

---

### 4. Sincronize a branch main do diretório local

 - No diretório local, retorne para a branch main e atualize com o diretório remoto.

```
git checkout main
git pull
```