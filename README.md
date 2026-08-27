# Delivery Tracker

Sistema full-stack para cadastro e acompanhamento de entregas. O repositório é um monorepo composto por uma API Spring Boot e uma interface Angular.

## Tecnologias

- Backend: Java 17, Spring Boot 4, Spring Web MVC, Spring Data JPA e PostgreSQL.
- Frontend: Angular 22, TypeScript e Angular Material.
- Testes: JUnit/Mockito no backend, Vitest no frontend e H2 em memória para testes da aplicação.

## Pré-requisitos

- Java 17 ou superior.
- Node.js `22.22.3+`, `24.15.0+` ou `26+`.
- npm 8 ou superior.
- PostgreSQL para executar a aplicação. Os testes automatizados não precisam dele.

## Clonando o projeto

```bash
git clone https://github.com/gabrwell/DeliveryTracker.git
cd DeliveryTracker
```

## Configurando o backend

A aplicação lê a conexão com o banco por variáveis de ambiente. No PowerShell:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/delivery_tracker"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "sua-senha"
$env:SPRING_PROFILES_ACTIVE = "dev"
```

O perfil `dev` insere três entregas de demonstração quando o banco está vazio. Não ative esse perfil em produção.

Para iniciar a API:

```powershell
cd delivery-tracker
.\mvnw.cmd spring-boot:run
```

A API ficará disponível em `http://localhost:8080/deliveries`.

Para executar os testes, não é necessário ter PostgreSQL instalado ou em execução:

```powershell
.\mvnw.cmd test
```

## Configurando o frontend

Em outro terminal:

```powershell
cd delivery-tracker-web
npm ci
npm start
```

A interface ficará disponível em `http://localhost:4200` e, no ambiente de desenvolvimento, acessará a API em `http://localhost:8080`.

Comandos de validação:

```powershell
npm run build
npm test -- --watch=false
npm audit --omit=dev
```

## Usando um PostgreSQL em outro computador

Altere `DB_URL` para o IP ou nome do computador que hospeda o banco:

```powershell
$env:DB_URL = "jdbc:postgresql://IP_DO_COMPUTADOR:5432/delivery_tracker"
```

O servidor PostgreSQL precisa aceitar conexões remotas, autorizar o IP do cliente no `pg_hba.conf` e ter a porta liberada no firewall. Fora de uma rede local confiável, use uma VPN privada ou túnel SSH; não exponha diretamente a porta 5432 na internet.

## Estrutura

```text
DeliveryTracker/
|-- delivery-tracker/       # API Spring Boot
|-- delivery-tracker-web/   # Aplicação Angular
|-- .github/workflows/      # Integração contínua
`-- README.md
```

## Fluxo de branches

- `main` deve permanecer estável.
- Funcionalidades devem usar branches curtas como `feature/nome-da-funcionalidade`.
- Correções devem usar branches como `fix/descricao-da-correcao`.
- Depois do merge por pull request, remova a branch remota já incorporada.
