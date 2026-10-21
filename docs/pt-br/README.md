# Expense Tracker

API de controle de despesas pessoais feita com Java 21 e Spring Boot. Cada pessoa cria uma conta, autentica com token JWT e acessa somente as próprias despesas.

[Read in English](../../README.md)

## Funcionalidades

- Cadastro e login com hash de senha BCrypt.
- Tokens JWT sem sessão no servidor.
- Criar, listar, consultar, editar e excluir despesas.
- Filtro mensal e resumo com quantidade e total calculados no banco.
- Validação de entrada e respostas de erro HTTP consistentes.
- Persistência PostgreSQL com migrações versionadas pelo Flyway.
- Documentação interativa OpenAPI.
- Ambiente local com Docker Compose.

## Tecnologias

Java 21, Spring Boot 3.5, Spring Web, Spring Data JPA, Spring Security, OAuth2 Resource Server, PostgreSQL, Flyway, Bean Validation, OpenAPI e Docker Compose.

## Executar localmente

Requisitos: Java 21 ou superior, Docker Desktop com Compose e Git.

1. Clone o repositório e entre na pasta.
2. Copie .env.example para .env.
3. No .env, configure valores próprios para POSTGRES_PASSWORD e JWT_SECRET. O segredo JWT deve ter pelo menos 32 caracteres aleatórios.
4. Suba a aplicação:

~~~powershell
docker compose up --build
~~~

A API fica em http://localhost:8080 e o Swagger UI em http://localhost:8080/swagger-ui.html. O Flyway aplica as migrações do banco na inicialização. Para parar os containers, use Ctrl+C ou docker compose down; o volume do banco é preservado.

Para iniciar via Maven, suba apenas o PostgreSQL com docker compose up -d db e execute ./mvnw spring-boot:run (Windows: mvnw.cmd spring-boot:run). Configure DB_URL, DB_USERNAME, DB_PASSWORD e JWT_SECRET no terminal.

A versão de console com JDBC usa o mesmo PostgreSQL e uma tabela própria chamada expense_v2. Inicie o Compose completo uma vez para o Flyway criar a tabela e depois execute:

~~~powershell
mvnw.cmd -Pconsole-v2 exec:java
~~~

Ao executar o console diretamente no Windows, configure DB_PASSWORD no terminal com o valor do arquivo .env.

## Rotas principais

Todas as rotas de despesas exigem o cabeçalho Authorization: Bearer <accessToken>.

| Método | Rota | Descrição |
| --- | --- | --- |
| POST | /api/v1/auth/register | Cadastra a conta e retorna um token |
| POST | /api/v1/auth/login | Autentica e retorna um token |
| POST | /api/v1/expenses | Cria uma despesa |
| GET | /api/v1/expenses | Lista as despesas da conta autenticada |
| GET | /api/v1/expenses?month=10&year=2026 | Filtra por mês |
| GET | /api/v1/expenses/monthly-summary?month=10&year=2026 | Retorna quantidade e total |
| GET | /api/v1/expenses/{id} | Consulta uma despesa própria |
| PUT | /api/v1/expenses/{id} | Atualiza uma despesa própria |
| DELETE | /api/v1/expenses/{id} | Exclui uma despesa própria |

### Exemplo de cadastro

~~~http
POST /api/v1/auth/register
Content-Type: application/json

{
  "name": "Marcio",
  "email": "marcio@example.com",
  "password": "troque-esta-senha"
}
~~~

A resposta inclui accessToken, tokenType e expiresInSeconds. Use o token Bearer nas rotas de despesas.

### Exemplo de criação de despesa

~~~http
POST /api/v1/expenses
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "description": "Mercado",
  "amount": 125.90,
  "date": "2026-10-29",
  "category": "FOOD"
}
~~~

Categorias válidas: FOOD, TRANSPORT, HOUSING, LEISURE, HEALTH e OTHER.

## Arquitetura

~~~mermaid
flowchart LR
    Cliente -->|cadastro / login| AuthController
    Cliente -->|Bearer JWT| Security
    Security --> ExpenseController
    AuthController --> AuthService
    AuthService --> UserRepository
    AuthService --> JwtService
    ExpenseController --> ExpenseService
    ExpenseService --> ExpenseRepository
    UserRepository --> PostgreSQL
    ExpenseRepository --> PostgreSQL
    Expense --> User
~~~

O token contém o ID da conta autenticada. As consultas de despesas sempre filtram pelo proprietário, impedindo que uma conta consulte ou altere registros de outra.

## Histórico do projeto

- src/v0: exercício de console procedural.
- src/v1: aplicação de console orientada a objetos com coleções.
- src/v2: aplicação de console com JDBC, DAO, persistência PostgreSQL e consultas mensais.
- src/main: API REST Spring Boot com JPA, autenticação JWT e isolamento dos dados por conta.

Os diagramas Mermaid em docs/diagrams/src descrevem as versões de console, JDBC e API REST.

## Segurança

- Senhas armazenadas como hash BCrypt.
- O segredo padrão do JWT em application.properties serve somente para desenvolvimento local.
- Configure um JWT_SECRET exclusivo antes de executar fora da máquina local; não faça commit do arquivo .env.
- A API usa autenticação Bearer sem sessão e valida assinatura, emissor e expiração do token.
