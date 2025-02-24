# 2. Criação das entidades e mapeamento

**Detalhamento da branch: 2. Criação das entidades e mapeamento (com Lombok)**

A proposta desta branch é implementar as entidades principais do projeto (`Categoria`, `Pessoa`, `Endereco` e `Lancamento`), definindo suas classes de modelo e mapeamentos para o banco de dados **utilizando as anotações do Lombok** para reduzir a quantidade de código boilerplate (getters, setters, construtores etc.).

---
### 1. Estrutura dos pacotes
   - Inclua a seguinte dependência no pom.xml 

```
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

---

### 2. Estrutura dos pacotes

- **Modelo (ou entidade)**  
  - `com.seuprojeto.model` (ou outro pacote definido para entidades)

Essa organização ajuda a manter o código bem separado e facilita a manutenção.

---

### 3. Criação da classe `Categoria`

1. **Nome da classe**: `Categoria`
2. **Atributos sugeridos**:  
   - `Long codigo`  
   - `String nome`

3. **Exemplo de implementação (usando Lombok, JPA e Bean Validation)**:
   ```java
      package com.seuprojeto.model;
      
      import jakarta.persistence.Entity;
      import jakarta.persistence.GeneratedValue;
      import jakarta.persistence.GenerationType;
      import jakarta.persistence.Id;
      import jakarta.persistence.Table;
      import jakarta.validation.constraints.NotNull;
      import jakarta.validation.constraints.Size;
      import lombok.AllArgsConstructor;
      import lombok.Data;
      import lombok.NoArgsConstructor;

      @Data
      @NoArgsConstructor
      @AllArgsConstructor
      @Entity
      @Table(name = "categoria")
      public class Categoria {
         @Id
         @GeneratedValue(strategy = GenerationType.IDENTITY)
         private Long codigo;

         @NotNull
         @Size(min = 3, max = 20)
         private String nome;
      }
   ```
   - **Observação:** As anotações `@NotNull` e `@Size` garantem validações de integridade dos dados.

---

### 4. Criação da classe `Endereco`

1. **Nome da classe**: `Endereco`  
   *Obs.: Evite o uso de acentos em nomes de classes para manter compatibilidade.*

2. **Atributos sugeridos**:  
   - `String logradouro`  
   - `String numero`  
   - `String complemento`  
   - `String bairro`  
   - `String cep`  
   - `String cidade`  
   - `String estado`

3. **Exemplo de implementação (usando Lombok e JPA)**:
   ```java
   package com.seuprojeto.model;

   import jakarta.persistence.Embeddable;
   import lombok.AllArgsConstructor;
   import lombok.Data;
   import lombok.NoArgsConstructor;

   @Data
   @NoArgsConstructor
   @AllArgsConstructor
   @Embeddable
   public class Endereco {
      
      private String logradouro;
      private String numero;
      private String complemento;
      private String bairro;
      private String cep;
      private String cidade;
      private String estado;
   }
   ```
---

### 5. Criação da classe `Pessoa`

1. **Nome da classe**: `Pessoa`
2. **Atributos sugeridos**:  
   - `Long codigo`  
   - `String nome`  
   - `Boolean ativo` (para indicar se a pessoa está ativa ou não)  
   - `Endereco endereco`

3. **Exemplo de implementação (usando Lombok, JPA e Bean Validation)**:
   ```java
   package com.seuprojeto.model;

   import jakarta.persistence.Embedded;
   import jakarta.persistence.Entity;
   import jakarta.persistence.GeneratedValue;
   import jakarta.persistence.GenerationType;
   import jakarta.persistence.Id;
   import jakarta.persistence.Table;
   import jakarta.validation.constraints.NotNull;
   import jakarta.validation.constraints.Size;

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

      @Embedded
      private Endereco endereco;
   }
   ```

---

### 6. Criação da enumeração `TipoLancamento`

1. **Nome da enumeração**: `TipoLancamento`
2. **Valores sugeridos**:  
   - `RECEITA`  
   - `DESPESA`

3. **Exemplo de implementação**:
   ```java
   package com.seuprojeto.model;

   public enum TipoLancamento {
       RECEITA,
       DESPESA
   }
   ```

---

### 7. Criação da classe `Lancamento`

1. **Nome da classe**: `Lancamento`
2. **Atributos sugeridos**:  
   - `Long codigo`  
   - `String descricao`  
   - `BigDecimal valor`  
   - `LocalDate dataVencimento`  
   - `LocalDate dataPagamento`  
   - `String observacao`  
   - `TipoLancamento tipo`  
   - `Categoria categoria`  
   - `Pessoa pessoa`

3. **Exemplo de implementação (usando Lombok, JPA e Bean Validation)**:
   ```java
   package com.seuprojeto.model;

   import lombok.AllArgsConstructor;
   import lombok.Data;
   import lombok.NoArgsConstructor;

   import javax.persistence.*;
   import javax.validation.constraints.NotNull;
   import java.math.BigDecimal;
   import java.time.LocalDate;

   import java.math.BigDecimal;
   import java.time.LocalDate;

   import jakarta.persistence.Entity;
   import jakarta.persistence.EnumType;
   import jakarta.persistence.Enumerated;
   import jakarta.persistence.GeneratedValue;
   import jakarta.persistence.GenerationType;
   import jakarta.persistence.Id;
   import jakarta.persistence.JoinColumn;
   import jakarta.persistence.ManyToOne;
   import jakarta.persistence.Table;
   import jakarta.validation.constraints.NotNull;
   import lombok.AllArgsConstructor;
   import lombok.Data;
   import lombok.NoArgsConstructor;

   @Data
   @NoArgsConstructor
   @AllArgsConstructor
   @Entity
   @Table(name = "lancamento")
   public class Lancamento {
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long codigo;

      @NotNull
      private String descricao;

      @NotNull
      private BigDecimal valor;

      @NotNull
      private LocalDate dataVencimento;

      private LocalDate dataPagamento;

      private String observacao;

      @NotNull
      @Enumerated(EnumType.STRING)
      private TipoLancamento tipo;

      @ManyToOne
      @JoinColumn(name = "categoria_codigo")
      private Categoria categoria;

      @ManyToOne
      @JoinColumn(name = "pessoa_codigo")
      private Pessoa pessoa;
   }
   ```
   - **Observação:**  
     - `@Enumerated(EnumType.STRING)` faz com que o valor da enumeração seja persistido como texto, facilitando a leitura e evitando problemas com a ordem dos enums.
---

### 8. Carga de dados com script data.sql
 - Crie o arquivo **data.sql** no diretório **resources**
 ```sql
 INSERT INTO categoria (nome) values ('Lazer');
INSERT INTO categoria (nome) values ('Alimentação');
INSERT INTO categoria (nome) values ('Supermercado');
INSERT INTO categoria (nome) values ('Farmácia');
INSERT INTO categoria (nome) values ('Outros');

INSERT INTO pessoa (nome, logradouro, numero, complemento, bairro, cep, cidade, estado, ativo) values ('João Silva', 'Rua do Abacaxi', '10', null, 'Brasil', '38.400-12', 'Uberlândia', 'MG', true);
INSERT INTO pessoa (nome, logradouro, numero, complemento, bairro, cep, cidade, estado, ativo) values ('Maria Rita', 'Rua do Sabiá', '110', 'Apto 101', 'Colina', '11.400-12', 'Ribeirão Preto', 'SP', true);
INSERT INTO pessoa (nome, logradouro, numero, complemento, bairro, cep, cidade, estado, ativo) values ('Pedro Santos', 'Rua da Bateria', '23', null, 'Morumbi', '54.212-12', 'Goiânia', 'GO', true);
INSERT INTO pessoa (nome, logradouro, numero, complemento, bairro, cep, cidade, estado, ativo) values ('Ricardo Pereira', 'Rua do Motorista', '123', 'Apto 302', 'Aparecida', '38.400-12', 'Salvador', 'BA', true);
INSERT INTO pessoa (nome, logradouro, numero, complemento, bairro, cep, cidade, estado, ativo) values ('Josué Mariano', 'Av Rio Branco', '321', null, 'Jardins', '56.400-12', 'Natal', 'RN', true);
INSERT INTO pessoa (nome, logradouro, numero, complemento, bairro, cep, cidade, estado, ativo) values ('Pedro Barbosa', 'Av Brasil', '100', null, 'Tubalina', '77.400-12', 'Porto Alegre', 'RS', true);
INSERT INTO pessoa (nome, logradouro, numero, complemento, bairro, cep, cidade, estado, ativo) values ('Henrique Medeiros', 'Rua do Sapo', '1120', 'Apto 201', 'Centro', '12.400-12', 'Rio de Janeiro', 'RJ', true);
INSERT INTO pessoa (nome, logradouro, numero, complemento, bairro, cep, cidade, estado, ativo) values ('Carlos Santana', 'Rua da Manga', '433', null, 'Centro', '31.400-12', 'Belo Horizonte', 'MG', true);
INSERT INTO pessoa (nome, logradouro, numero, complemento, bairro, cep, cidade, estado, ativo) values ('Leonardo Oliveira', 'Rua do Músico', '566', null, 'Segismundo Pereira', '38.400-00', 'Uberlândia', 'MG', true);
INSERT INTO pessoa (nome, logradouro, numero, complemento, bairro, cep, cidade, estado, ativo) values ('Isabela Martins', 'Rua da Terra', '1233', 'Apto 10', 'Vigilato', '99.400-12', 'Manaus', 'AM', true);

INSERT INTO lancamento (descricao, data_vencimento, data_pagamento, valor, observacao, tipo, categoria_codigo, pessoa_codigo) values ('Salário mensal', '2017-06-10', null, 6500.00, 'Distribuição de lucros', 'RECEITA', 1, 1);
INSERT INTO lancamento (descricao, data_vencimento, data_pagamento, valor, observacao, tipo, categoria_codigo, pessoa_codigo) values ('Bahamas', '2017-02-10', '2017-02-10', 100.32, null, 'DESPESA', 2, 2);
INSERT INTO lancamento (descricao, data_vencimento, data_pagamento, valor, observacao, tipo, categoria_codigo, pessoa_codigo) values ('Top Club', '2017-06-10', null, 120, null, 'RECEITA', 3, 3);
INSERT INTO lancamento (descricao, data_vencimento, data_pagamento, valor, observacao, tipo, categoria_codigo, pessoa_codigo) values ('CEMIG', '2017-02-10', '2017-02-10', 110.44, 'Geração', 'RECEITA', 3, 4);
INSERT INTO lancamento (descricao, data_vencimento, data_pagamento, valor, observacao, tipo, categoria_codigo, pessoa_codigo) values ('DMAE', '2017-06-10', null, 200.30, null, 'DESPESA', 3, 5);
INSERT INTO lancamento (descricao, data_vencimento, data_pagamento, valor, observacao, tipo, categoria_codigo, pessoa_codigo) values ('Extra', '2017-03-10', '2017-03-10', 1010.32, null, 'RECEITA', 4, 6);
INSERT INTO lancamento (descricao, data_vencimento, data_pagamento, valor, observacao, tipo, categoria_codigo, pessoa_codigo) values ('Bahamas', '2017-06-10', null, 500, null, 'RECEITA', 1, 7);
INSERT INTO lancamento (descricao, data_vencimento, data_pagamento, valor, observacao, tipo, categoria_codigo, pessoa_codigo) values ('Top Club', '2017-03-10', '2017-03-10', 400.32, null, 'DESPESA', 4, 8);
INSERT INTO lancamento (descricao, data_vencimento, data_pagamento, valor, observacao, tipo, categoria_codigo, pessoa_codigo) values ('Despachante', '2017-06-10', null, 123.64, 'Multas', 'DESPESA', 3, 9);
INSERT INTO lancamento (descricao, data_vencimento, data_pagamento, valor, observacao, tipo, categoria_codigo, pessoa_codigo) values ('Pneus', '2017-04-10', '2017-04-10', 665.33, null, 'RECEITA', 5, 10);
INSERT INTO lancamento (descricao, data_vencimento, data_pagamento, valor, observacao, tipo, categoria_codigo, pessoa_codigo) values ('Café', '2017-06-10', null, 8.32, null, 'DESPESA', 1, 5);
INSERT INTO lancamento (descricao, data_vencimento, data_pagamento, valor, observacao, tipo, categoria_codigo, pessoa_codigo) values ('Eletrônicos', '2017-04-10', '2017-04-10', 2100.32, null, 'DESPESA', 5, 4);
INSERT INTO lancamento (descricao, data_vencimento, data_pagamento, valor, observacao, tipo, categoria_codigo, pessoa_codigo) values ('Instrumentos', '2017-06-10', null, 1040.32, null, 'DESPESA', 4, 3);
INSERT INTO lancamento (descricao, data_vencimento, data_pagamento, valor, observacao, tipo, categoria_codigo, pessoa_codigo) values ('Café', '2017-04-10', '2017-04-10', 4.32, null, 'DESPESA', 4, 2);
INSERT INTO lancamento (descricao, data_vencimento, data_pagamento, valor, observacao, tipo, categoria_codigo, pessoa_codigo) values ('Lanche', '2017-06-10', null, 10.20, null, 'DESPESA', 4, 1);
 ```

 ---

### 9. Alterando o arquivo application.properties
 - Adicione as seguintes linhas ao seu application.properties:
 ```
 #quando usar o data.sql para popular o banco
 spring.jpa.defer-datasource-initialization=true
 spring.sql.init.encoding=UTF-8
```
**Explicação:**

spring.jpa.defer-datasource-initialization=true

- Esta propriedade adia a inicialização do datasource até que o contexto do JPA esteja completamente configurado. Assim, evita-se que o script SQL seja executado antes das entidades estarem totalmente mapeadas, prevenindo possíveis conflitos na criação do schema.

spring.sql.init.encoding=UTF-8

- Define a codificação usada para ler os arquivos SQL de inicialização (como o data.sql). Com essa configuração, os caracteres especiais e acentuação são interpretados corretamente, garantindo a integridade dos dados carregados no banco.

---

### 10. Execute a aplicação e verifique se os dados foram carregados no banco de dados H2

- Após iniciar a aplicação, acesse o console do H2 ( disponível em [http://localhost:8080/h2-console](http://localhost:8080/h2-console)) para confirmar que os dados do arquivo **data.sql** foram inseridos corretamente. 
- Utilize as credenciais configuradas em seu projeto para realizar o login e navegue pelas tabelas para verificar os registros.

---

### 11. Realize o commit, push e abra um Pull Request para essa issue

- Após validar que os dados foram carregados corretamente, efetue o commit das alterações e faça o push para o repositório remoto. 
```
git add .
git commit -m "2. Criação das entidades e mapeamento"
git push 
```

- Em seguida, abra um Pull Request (PR) na branch de destino, descrevendo as alterações realizadas. Certifique-se de que o PR esteja de acordo com as diretrizes do projeto para revisão e integração.

### 12. Sincronize a branch main do diretório local
- No diretório local, retorn para a branch main e atualize com o diretório remoto.
```
git checkout main
git pull
```