# Tutorial: 10. Validações 

## 1. Introdução

O Swagger (por meio da especificação OpenAPI) permite documentar, testar e visualizar interativamente os endpoints da sua API. No ecossistema Spring Boot, a forma mais atual e simples de integrar essa documentação é usando o [SpringDoc OpenAPI](https://springdoc.org/).

## 2. Criação da Branch no Git

No repositório remoto (GitHub), crie uma branch para implementar a nova issue. Em seguida, no ambiente local, execute os comandos:

```bash
git fetch origin
git checkout 10-10-documentação-e-testes
```

Observação: Utilize o mesmo nome da branch tanto no repositório remoto quanto no local. Neste exemplo, usamos um nome sem acentos para evitar problemas de encoding.

---

## 3. Adicionando a Dependência

Se o seu projeto utiliza Maven, adicione a dependência do SpringDoc OpenAPI no arquivo `pom.xml`:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.7.0</version>
</dependency>
```

> Essas dependências são responsáveis por gerar a documentação OpenAPI e disponibilizar a interface do Swagger UI automaticamente.

---

## 4. Configurando a Documentação

Após adicionar a dependência, o SpringDoc já configura a documentação da sua API de forma automática. Por padrão:

- O arquivo JSON da documentação estará disponível em:  
  `http://localhost:8080/v3/api-docs`
- A interface interativa do Swagger UI poderá ser acessada em:  
  `http://localhost:8080/swagger-ui/index.html`

Caso queira customizar algum aspecto (como título, descrição, versão, etc.), você pode criar um arquivo de configuração ou definir propriedades no `application.properties` ou `application.yml`. Por exemplo, adicionando no `application.properties`:

```properties
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

---

## 5. Documentando os Endpoints

Para enriquecer a documentação, utilize as anotações do pacote `io.swagger.v3.oas.annotations` em seus controllers. Veja um exemplo:

```java
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Arrays;
import java.util.List;

@RestController
public class UserController {

    @Operation(summary = "Retorna a lista de usuários", description = "Endpoint para obter todos os usuários cadastrados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/users")
    public List<String> getUsers() {
        return Arrays.asList("João", "Maria", "Pedro");
    }
}
```

> As anotações `@Operation` e `@ApiResponses` ajudam a detalhar o que cada endpoint faz, quais respostas ele pode retornar, entre outras informações importantes.

---

## 6. Testando a Documentação

1. **Inicie a aplicação Spring Boot.**
2. **Acesse o Swagger UI:**  
   Abra o navegador e vá para [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html).

Na interface do Swagger UI, você encontrará a documentação interativa da sua API, com a possibilidade de testar os endpoints diretamente.

---

## 7. Considerações Finais

- **Atualização Automática:** Cada vez que você modificar os endpoints ou adicionar novas anotações, a documentação é atualizada automaticamente.
- **Customização:** O SpringDoc permite personalizações adicionais, como a criação de um grupo de APIs ou a inclusão de informações de segurança. Consulte a [documentação oficial do SpringDoc](https://springdoc.org/) para mais detalhes.
- **Outras Anotações:** Além das mostradas, existem outras anotações úteis, como `@Parameter` para parâmetros e `@Schema` para descrever modelos, que podem enriquecer ainda mais sua documentação.

Com esses passos, você conseguirá integrar a documentação Swagger em seu projeto Spring Boot existente, facilitando o entendimento e o consumo da API por outros desenvolvedores.

--- 

## 8. Commit, Push e Pull Request

Após confirmar que a API está funcionando conforme esperado:

1. Faça o commit das alterações:
   ```bash
   git add .
   git commit -m "10-10-documentação-e-testes"
   ```
2. Envie as alterações para o repositório remoto:
   ```bash
   git push
   ```
3. Abra um Pull Request (PR) na branch de destino, seguindo as diretrizes do projeto para revisão e integração.

---

## 9. Sincronizando a Branch Main no Ambiente Local

Após a integração, atualize sua branch main:

```bash
git checkout main
git pull
```