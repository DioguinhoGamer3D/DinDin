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
- [ ] Fase 3 — CRUD de categorias e transações
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
