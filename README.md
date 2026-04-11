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
