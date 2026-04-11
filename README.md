# API Pagamentos

Guia rapido para executar a API, acessar a documentacao e autenticar.

## Pre-requisitos

## Execucao em Producao (VPS/Hostinger)

Arquivos usados:

- `Dockerfile`
- `docker-compose.prod.yml`
- `.env.example`
- `nginx.conf`
- `src/main/resources/application-prod.properties`

### 1) Preparar variaveis de ambiente

No diretorio do projeto:

```bash
cp .env.example .env
```

Edite o arquivo `.env` e ajuste principalmente:

- `DB_PASSWORD`
- `SECURITY_JWT_SECRET_KEY`
- `APP_CORS_ALLOWED_ORIGINS`
- `SERVER_NAME` (se usar dominio no Nginx)

### 2) Subir containers

```bash
docker compose -f docker-compose.prod.yml --env-file .env up -d --build
```

### 3) Verificar status

```bash
docker compose -f docker-compose.prod.yml ps
docker compose -f docker-compose.prod.yml logs -f api
```

### 4) Endpoint de saude

Com o stack no ar:

```text
http://SEU_DOMINIO/actuator/health
```

ou local:

```text
http://IP_DA_VPS/actuator/health
```

### 5) Atualizar deploy

Depois de atualizar o codigo:

```bash
docker compose -f docker-compose.prod.yml --env-file .env up -d --build
```

### Observacoes

- `application-prod.properties` esta configurado para PostgreSQL via variaveis (`DB_URL`, `DB_USER`, `DB_PASSWORD`).
- Swagger e H2 estao desabilitados em producao.
- Se usar HTTPS, adicione certificados no Nginx e publique a porta `443`.

---

## Execucao Local (Erro security.jwt.secret-key)

Use uma destas opcoes:

### 1) Definir via variavel de ambiente (rapido no PowerShell)

```powershell
$env:SECURITY_JWT_SECRET_KEY="3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b"
$env:SECURITY_JWT_EXPIRATION_TIME="360000000"
.\mvnw.cmd spring-boot:run
```

### 2) Garantir no application.properties (ou application-dev.properties)

```properties
security.jwt.secret-key=${SECURITY_JWT_SECRET_KEY:3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b}
security.jwt.expiration-time=${SECURITY_JWT_EXPIRATION_TIME:360000000}
```

Se quiser, te passo tambem uma versao com `.env` + comando unico para subir sempre sem precisar exportar variavel manualmente.

---
## 1. Criação da Branch no Git

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

## Endpoint Dashboard

Endpoint para montar graficos no frontend:

- `GET /dashboard?dataInicio=YYYY-MM-DD&dataFim=YYYY-MM-DD`

Exemplo:

```bash
curl -X GET "http://localhost:8080/dashboard?dataInicio=2024-01-01&dataFim=2024-03-31" \
  -H "Authorization: Bearer <jwt>"
```

Campos principais retornados:

- `receitas.total` e `receitas.porCategoria[]`
- `despesas.total` e `despesas.porCategoria[]`
- `top10Receitas[]` e `top10Despesas[]`
- `receitasPagasMensal[]` e `despesasPagasMensal[]`

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
