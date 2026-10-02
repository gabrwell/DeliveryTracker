# Delivery Tracker

API REST backend-first para cadastrar entregas, consultar códigos de rastreio, controlar transições de status e manter o histórico de cada pedido. O frontend Angular funciona como cliente demonstrativo da API.

## Funcionalidades

- Cadastro de entregas com código de rastreio automático.
- Consulta de entregas pelo código.
- Rastreamento público com código, status e datas, sem expor o destinatário.
- Atualização controlada entre os status `CREATED`, `IN_TRANSIT`, `DELIVERED` e `CANCELED`.
- Histórico persistido de todas as mudanças de status.
- Validação de dados e respostas de erro padronizadas.
- Operações administrativas protegidas pelo perfil `OPERATOR`.

## Principais endpoints

O rastreamento público não exige autenticação:

- `GET /tracking/{trackingCode}`: consulta código, status e datas da entrega.

As rotas abaixo exigem um operador autenticado:

- `POST /deliveries`: cadastra uma entrega.
- `GET /deliveries`: lista entregas com paginação e filtros opcionais por `status`,
  `recipient`, `createdFrom` e `createdTo`.
- `GET /deliveries/{trackingCode}`: consulta uma entrega.
- `PATCH /deliveries/{trackingCode}/status`: atualiza o status.
- `GET /deliveries/{trackingCode}/history`: consulta o histórico de status em ordem cronológica.
- `GET /auth/csrf`: obtém o token CSRF para operações de escrita.

Neste checkpoint a autenticação usa HTTP Basic com um operador em memória,
configurado por variáveis de ambiente. Ainda não há login JWT nem usuários
persistidos. Veja o [guia deste checkpoint](docs/spring-security.md).

Exemplo de listagem filtrada:

```http
GET /deliveries?status=IN_TRANSIT&recipient=Gabriel&page=0&size=10&sort=trackingCode,asc
```

## Tecnologias

- **Backend:** Java 17, Spring Boot, Spring Security, Spring Data JPA e PostgreSQL.
- **Frontend:** Angular, TypeScript e Angular Material.
- **Testes:** JUnit, Mockito, Vitest e H2.

## Como executar

### 1. Clone o repositório

```bash
git clone https://github.com/gabrwell/DeliveryTracker.git
cd DeliveryTracker
```

### 2. Inicie o backend

Crie no PostgreSQL um banco chamado `delivery_tracker` e configure a conexão no PowerShell:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/delivery_tracker"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "sua-senha"
$env:SPRING_PROFILES_ACTIVE = "dev"
$env:OPERATOR_USERNAME = "operador"
$deliveryOperatorPassword = Read-Host "Senha do operador (mínimo de 12 caracteres)" -AsSecureString
$env:OPERATOR_PASSWORD = [System.Net.NetworkCredential]::new("", $deliveryOperatorPassword).Password

cd delivery-tracker
.\mvnw.cmd spring-boot:run
```

A API estará disponível em `http://localhost:8080`. Um exemplo de rastreamento
público no perfil `dev` é `http://localhost:8080/tracking/BR100200300SP`.

Para testar `/deliveries` no Postman, use a autenticação Basic com o operador
configurado. Para `POST` e `PATCH`, faça primeiro `GET /auth/csrf` com essa
autenticação, preserve o cookie de sessão e envie o valor de `token` no header
indicado por `headerName` (`X-CSRF-TOKEN`).

Sem `OPERATOR_USERNAME` e `OPERATOR_PASSWORD`, o rastreamento público continua
disponível, mas nenhum operador é criado. As duas variáveis devem ser definidas
juntas; a senha precisa ter pelo menos 12 caracteres e no máximo 72 bytes UTF-8.
Em conexões fora de `localhost`, use HTTPS para transmitir as credenciais Basic.

Se o banco já foi utilizado por uma versão anterior do projeto, defina
`$env:FLYWAY_BASELINE_ON_MIGRATE = "true"` somente na primeira inicialização com Flyway.

### 3. Inicie o frontend

Em outro terminal:

```powershell
cd delivery-tracker-web
npm ci
npm start
```

A aplicação estará disponível em `http://localhost:4200`.

O cliente Angular ainda não possui integração de autenticação. Neste checkpoint,
suas chamadas a `/deliveries` retornam `401`; o acesso público a `/tracking` pode
ser testado diretamente no navegador ou no Postman. A integração do frontend
será feita após o login JWT.

## Testes

Os testes do backend utilizam H2 e não dependem do PostgreSQL:

```powershell
cd delivery-tracker
.\mvnw.cmd test
```

Para executar os testes do frontend:

```powershell
cd delivery-tracker-web
npm test -- --watch=false
```
