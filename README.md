# DinDin
Sistema de controle de gastos e finanças pessoais (web app / PWA).

## 🚀 Tecnologias

### Backend
- Java 21
- Spring Boot 4
- PostgreSQL (hospedado no [Neon](https://neon.tech))
- Spring Data JPA + Flyway
- Spring Security + JWT (jjwt)
- Lombok

### Frontend
- React
- TypeScript
- Tailwind CSS
- Vite

## 📁 Estrutura

```
DinDin/
├── backend/
└── frontend/
```

## ✅ Status do projeto

- [x] **Fase 1** — Modelo de dados (entidades JPA, migrations Flyway, repositórios)
- [x] **Fase 2** — Autenticação JWT (registro, login, rotas protegidas)
- [x] **Fase 3** — CRUD de categorias e transações
- [ ] Fase 4-7 — Frontend, PWA e deploy

## ⚙️ Configuração

O backend precisa das seguintes variáveis de ambiente:

| Variável | Descrição |
|---|---|
| `DB_USERNAME` | Usuário do banco PostgreSQL (Neon) |
| `DB_PASSWORD` | Senha do banco PostgreSQL (Neon) |
| `JWT_SECRET` | Chave secreta usada para assinar os tokens JWT |

No IntelliJ, configure essas variáveis em **Run/Debug Configurations → Environment variables**.

### Rodando o backend

```bash
cd backend
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

### Rodando os testes

```bash
cd backend
./mvnw test
```

Os testes de repositório usam [Testcontainers](https://testcontainers.com/) e exigem o **Docker Desktop** rodando.

## 🔐 Autenticação (Fase 2)

| Método | Rota | Descrição | Protegida? |
|---|---|---|---|
| POST | `/auth/register` | Cria um novo usuário e retorna um token JWT | Não |
| POST | `/auth/login` | Autentica um usuário existente e retorna um token JWT | Não |

Todas as demais rotas exigem o header:
```
Authorization: Bearer <token>
```

**Regras de senha:** mínimo 8 caracteres, pelo menos 1 letra maiúscula e 1 número.

## 📚 Endpoints (Fase 3)

Todas as rotas abaixo exigem o header `Authorization: Bearer <token>`.
Dados de outro usuário nunca são expostos: o acesso a um recurso alheio retorna **404**.

### Categorias

| Método | Rota | Descrição | Respostas |
|---|---|---|---|
| GET | `/categorias` | Lista as categorias do usuário | 200 |
| POST | `/categorias` | Cria uma categoria | 201, 400 |
| PUT | `/categorias/{id}` | Atualiza uma categoria | 200, 400, 404 |
| DELETE | `/categorias/{id}` | Remove uma categoria | 204, 404, 409 (em uso) |

### Transações

| Método | Rota | Descrição | Respostas |
|---|---|---|---|
| GET | `/transacoes?mes=AAAA-MM` | Lista as transações do mês, mais recentes primeiro | 200, 400 |
| POST | `/transacoes` | Cria uma transação | 201, 400, 404 |
| PUT | `/transacoes/{id}` | Atualiza uma transação | 200, 400, 404 |
| DELETE | `/transacoes/{id}` | Remove uma transação | 204, 404 |

**Regras:** o `tipo` da transação deve ser igual ao tipo da categoria (400 se diferir);
a categoria deve pertencer ao usuário (404 se não); `valor` deve ser positivo, com até 2 casas decimais.