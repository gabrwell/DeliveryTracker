# Spring Security: checkpoint 1

Este checkpoint separa o rastreamento público das operações de operador e
introduz autenticação e autorização no backend. A conta desta etapa fica em
memória e é configurada por ambiente; usuários no PostgreSQL e login JWT serão
implementados em um próximo checkpoint.

## Quem pode acessar

| Operação | Visitante | Operador |
| --- | --- | --- |
| `GET /tracking/{trackingCode}` | Permitido | Permitido |
| `GET /deliveries` | `401` | Permitido |
| `GET /deliveries/{trackingCode}` | `401` | Permitido |
| `GET /deliveries/{trackingCode}/history` | `401` | Permitido |
| `POST /deliveries` | `401` | Permitido com CSRF |
| `PATCH /deliveries/{trackingCode}/status` | `401` | Permitido com CSRF |
| `GET /auth/csrf` | `401` | Permitido |

Um usuário autenticado sem `ROLE_OPERATOR` recebe `403` nas operações de
operador. Rotas que não foram explicitamente autorizadas são bloqueadas.
O perfil de administrador ainda não foi implementado.

## Autenticação e autorização

Autenticação identifica quem fez a requisição. Nesta etapa, HTTP Basic envia
usuário e senha no header `Authorization`. Basic não criptografa essas
credenciais: conexões fora de localhost devem usar HTTPS.

Autorização verifica se essa identidade pode executar a operação. Por isso,
`authenticated()` sozinho não representa a regra do projeto: usamos
`hasRole("OPERATOR")`. O Spring representa esse perfil pela authority
`ROLE_OPERATOR`.

## Caminho da requisição

```text
Requisição
  -> processamento de CORS e proteção CSRF
  -> autenticação HTTP Basic
  -> verificação da rota e do perfil
  -> Controller
  -> Service e regras de domínio
  -> Repository e banco
```

`SecurityFilterChain` define essa sequência. Os filtros executam antes dos
controllers; por isso o `ControllerAdvice` não basta para tratar falhas de
autenticação. `SecurityErrorHandler` produz o mesmo formato `timestamp`,
`status` e `message` usado pelos outros erros da API.

- `401`: credenciais ausentes ou inválidas.
- `403`: usuário autenticado sem permissão, ou operação rejeitada por CSRF.
- `404`: código de rastreamento inexistente.
- `409`: operador autenticado solicita uma transição de status inválida.

## Conta temporária de operador

`OperatorProperties` lê `OPERATOR_USERNAME` e `OPERATOR_PASSWORD`. Sem as duas
variáveis, o sistema não cria uma conta e mantém protegidas as operações
administrativas. Uma configuração incompleta interrompe a inicialização.

`InMemoryUserDetailsManager` guarda a conta durante a execução da aplicação.
A senha é codificada com BCrypt antes de ser armazenada nesse gerenciador.
Não existe conta com senha padrão, cadastro público ou tabela de usuários nesta
etapa. Reiniciar a aplicação recria a conta usando as variáveis de ambiente.

## CSRF e CORS

CSRF permanece ativo porque o navegador pode enviar credenciais Basic
automaticamente. Para uma escrita autenticada:

1. Fazer `GET /auth/csrf` com autenticação Basic.
2. Guardar o cookie de sessão enviado pelo servidor.
3. Fazer `POST` ou `PATCH` com a mesma sessão, autenticação Basic e o token no
   header retornado em `headerName`.

CSRF é um token de proteção contra requisições forjadas; não é um JWT nem um
token de login. O cookie desta etapa permite ao servidor guardar o token CSRF.

CORS foi centralizado em `SecurityConfig` para permitir os preflights do
frontend antes da autenticação. `CORS_ALLOWED_ORIGINS` aceita origens explícitas
separadas por vírgula. CORS não identifica usuários nem substitui permissões.

## Testes e próximos passos

Os testes existentes agora simulam um operador com `@WithMockUser` e usam
`csrf()` nas escritas. Isso mantém o foco desses testes nas regras de domínio.

`SecurityIntegrationTest` testa visitantes, usuários sem permissão, credenciais
Basic reais, CORS, privacidade do rastreamento e uma escrita com um token CSRF
real emitido pela API. As credenciais dessa classe são fixtures exclusivas de
teste.

O próximo checkpoint deve criar usuários persistidos com Flyway, definir o
fluxo de login e configurar emissão/validação de JWT. Só então o frontend
receberá uma tela de login e a integração de autenticação. As chamadas atuais
do Angular a `/deliveries` já estão protegidas e recebem `401` sem autenticação.

Referências oficiais:

- [Autorização de requisições](https://docs.spring.io/spring-security/reference/7.0/servlet/authorization/authorize-http-requests.html)
- [HTTP Basic](https://docs.spring.io/spring-security/reference/7.0/servlet/authentication/passwords/basic.html)
- [CSRF](https://docs.spring.io/spring-security/reference/7.0/servlet/exploits/csrf.html)
- [CORS](https://docs.spring.io/spring-security/reference/7.0/servlet/integrations/cors.html)
