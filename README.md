# 1. Configuração inicial do projeto

**Passo a passo para a configuração inicial do projeto**  

1. **Instalar o JDK Amazon Corretto 17**  
   - Acesse o site oficial da Amazon Corretto (https://aws.amazon.com/corretto/).  
   - Baixe a versão correspondente ao seu sistema operacional (Windows, Linux ou macOS).  
   - Faça a instalação seguindo as instruções do assistente (no Windows) ou utilize os comandos adequados no Linux/macOS.  
   - Verifique a instalação abrindo o terminal/prompt de comando e executando `java -version`.  

2. **Instalar o Git**  
   - Acesse o site oficial do Git (https://git-scm.com/downloads).  
   - Baixe o instalador para o seu sistema operacional.  
   - Conclua a instalação e configure suas credenciais (nome e e-mail) no Git:  
     ```
     git config --global user.name "Seu Nome"
     git config --global user.email "seu_email@exemplo.com"
     ```  
   - Verifique a instalação executando `git --version` no terminal/prompt de comando.  

3. **Instalar o Visual Studio Code (VS Code)**  
   - Acesse https://code.visualstudio.com/download.  
   - Baixe e instale a versão correspondente ao seu sistema operacional.  
   - Após a instalação, abra o VS Code.  

4. **Instalar as extensões necessárias no VS Code**  
   - Abra o VS Code e vá em **Extensions** (ícone de quadradinho do lado esquerdo).  
   - Pesquise e instale as seguintes extensões:
     - **Extension Pack for Java** (fornece suporte básico para desenvolvimento Java).  
     - **Spring Boot Extension Pack** (para recursos avançados do Spring).  
     - **Portuguese (Brazil) Language Pack for Visual Studio Code** (caso queira a interface em português).  
     - **vscode-icons** (para melhorar a visualização dos ícones dos arquivos/projetos).  

5. **Criar o repositório no GitHub**  
   - Acesse sua conta no GitHub e crie um novo repositório (público ou privado): 
   ```
   api-pagamentos
   ```  
   - Configure o gerenciamento do projeto criando o projeto, as Milestones e as Issues. 

6. **Criar o projeto utilizando o Spring Initializr**  
   - Acesse o site do Spring Initializr (https://start.spring.io/) ou utilize a integração do Spring no VS Code.  
   - Configure as opções:  
     - **Project**: Maven Project  
     - **Language**: Java  
     - **Spring Boot**: Versão LTS ou a mais recente estável  
     - **Group**: com.seuprojeto (exemplo)  
     - **Artifact**: api-pagamentos (exemplo)  
     - **Name**: api-pagamentos (exemplo)  
     - **Dependencies**:  
       - Spring Web  
       - H2 Database  
       - Spring Data JPA  
       - Lombok  
       - Spring Boot DevTools  
   - Gere o projeto diretamente na pasta.  

7. **Primeiro commit e push do projeto**  
   - Abra o terminal no VS Code (ou outro terminal) e execute:
    ```
    echo "# api-pagamentos" >> README.md
    git init
    git add README.md
    git commit -m "first commit"
    git branch -M main
    git remote add origin git@github.com:seu-usuario/api-pagamentos.git
    git push -u origin main
    ```
   - Verifique no GitHub se o projeto foi enviado corretamente para o repositório remoto.  

8. **Criar a branch da Feature: 1. Configuração inicial do projeto**
   - No repositório, abra a issue e crie a branch
   - Execute no terminal do VS Code os seguintes comandos para criar a branch no repositório local
   ```
   git fetch origin
   git checkout nome-da-branch
   ```

9. **Configurar o arquivo `application.properties`**  
   - Localize o arquivo `src/main/resources/application.properties`.  
   - Adicione as propriedades básicas de configuração do banco de dados H2 e de JPA, por exemplo:
     ```
        server.port=8080
        spring.application.name=api-pagamentos
        # Banco de dados H2
        spring.datasource.url=jdbc:h2:mem:gerenciador_pagamentos
        spring.datasource.driverClassName=org.h2.Driver
        spring.datasource.username=sa
        spring.datasource.password=
        spring.h2.console.enabled=true
        spring.h2.console.path=/h2-console
        # JPA anotações
        spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
        spring.jpa.show-sql=true
        spring.jpa.hibernate.ddl-auto=update
     ```
   - Ajuste conforme suas preferências de conexão, se necessário (por exemplo, `spring.jpa.hibernate.ddl-auto=create-drop` para recriar o banco a cada execução durante o desenvolvimento).  

10. **Crie um endpoint de teste**  
  - Crie o seguinte endpoint de teste
```
@RestController
@RequestMapping("/api/pagamentos")
public class TesteController {
    
    // Teste de API de pagamentos
    @RequestMapping("/teste")
    public String teste() {
        return "Teste de API de pagamentos";
    }
}
```

11. **Verificar o funcionamento**  
   - Na raiz do projeto, execute:
     ```
     ./mvnw spring-boot:run
     ```
     ou
     ```
     mvn spring-boot:run
     ```
   - Aguarde a aplicação iniciar. Se tudo estiver correto, ela ficará disponível em `http://localhost:8080/api/pagamentos/teste`.  
   - Acesse `http://localhost:8080/h2-console` para abrir o console do H2 e confirmar que o banco de dados está funcionando.

12. **Commit e push da Feature**  
  - Abra o terminal no VS Code (ou outro terminal) e execute:
  ```
  git add .
  git commit -m "Configuração inicial do projeto"
  git push 
  ```

13. **Crie o Pull Request no GitHub**  
  - No GitHub, vá até a página do seu repositório.
  - Se a branch foi enviada corretamente, o GitHub normalmente exibe um botão Compare & pull request ou algo similar. Clique nele.
  - Se não estiver visível, clique em Pull requests e, em seguida, em New pull request.
  - Escolha a branch de destino (normalmente a main ou develop) e a sua branch de feature.
  - Adicione o título e a descrição do seu PR, detalhando as alterações.
  - Clique em Create pull request.
  - Revisão, feedback e merge

  - Seus colegas (ou você mesmo) podem revisar o código e fazer comentários.
  - Quando o PR for aprovado, você (ou alguém com permissões adequadas) pode fazer o merge (união) do PR na branch de destino.

14. **Sincronize a branch main do diretório local**
   - No diretório local, retorn para a branch main e atualize com o diretório remoto.
   ```
   git checkout main
   git pull
   ``` 
