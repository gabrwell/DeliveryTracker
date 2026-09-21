# Delivery Tracker

API REST backend-first para cadastrar entregas, consultar códigos de rastreio, controlar transições de status e manter o histórico de cada pedido. O frontend Angular funciona como cliente demonstrativo da API.

## Funcionalidades

- Cadastro de entregas com código de rastreio automático.
- Consulta de entregas pelo código.
- Atualização controlada entre os status `CREATED`, `IN_TRANSIT`, `DELIVERED` e `CANCELED`.
- Histórico persistido de todas as mudanças de status.
- Validação de dados e respostas de erro padronizadas.

## Principais endpoints

- `POST /deliveries`: cadastra uma entrega.
- `GET /deliveries`: lista entregas com paginação.
- `GET /deliveries/{trackingCode}`: consulta uma entrega.
- `PATCH /deliveries/{trackingCode}/status`: atualiza o status.
- `GET /deliveries/{trackingCode}/history`: consulta o histórico de status em ordem cronológica.

## Tecnologias

- **Backend:** Java 17, Spring Boot, Spring Data JPA e PostgreSQL.
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

cd delivery-tracker
.\mvnw.cmd spring-boot:run
```

A API estará disponível em `http://localhost:8080/deliveries`.

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
