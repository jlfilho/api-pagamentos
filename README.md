# API Pagamentos

Guia rapido para executar a API, acessar a documentacao e autenticar.

## Pre-requisitos

- Java 17 ou superior
- Windows PowerShell
- Porta `8080` livre

## Execucao via CLI

No diretorio raiz do projeto:

```powershell
.\mvnw.cmd spring-boot:run
```

Executar com perfil especifico:

```powershell
$env:SPRING_PROFILES_ACTIVE='dev'; .\mvnw.cmd spring-boot:run
```

```powershell
$env:SPRING_PROFILES_ACTIVE='prod'; .\mvnw.cmd spring-boot:run
```

## Documentacao da API

Com a API rodando na porta `8080`:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Observacao: no perfil `prod`, a documentacao pode estar desabilitada por configuracao.

## Login de Acesso

Endpoint de autenticacao:

- `POST /api/auth/login`

Exemplo de requisicao:

```json
{
  "username": "admin@uea.edu.br",
  "password": "admin123"
}
```

Exemplo com cURL:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin@uea.edu.br","password":"admin123"}'
```

Resposta esperada:

```json
{
  "token": "<jwt>",
  "expiresIn": 3600000
}
```

Use o token nos endpoints protegidos:

```http
Authorization: Bearer <jwt>
```
