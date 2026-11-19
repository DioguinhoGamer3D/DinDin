# DinDin

Sistema de controle de gastos e finanças pessoais, em formato de web app instalável (PWA). Cada usuário tem a sua conta, as suas categorias e as suas transações, e os dados ficam sincronizados entre celular e computador.

**Acesse:** https://din-din-ochre-one.vercel.app/login

> O backend roda no plano gratuito da Render, que "dorme" após 15 minutos sem uso. Então o primeiro acesso depois disso pode levar cerca de um minuto.

## ✨ Funcionalidades

- Cadastro e login com JWT
- Categorias de entrada e saída, cada uma com a sua cor
- Transações com descrição, valor, data, categoria e observações
- Busca e filtros por categoria e tipo, sempre por mês
- Dashboard com saldo, entradas, saídas, taxa de poupança, gráfico semanal e despesas por categoria
- Instalável no celular (PWA)

## 🚀 Tecnologias

**Backend:** Java 21, Spring Boot 4, Spring Security + JWT, Spring Data JPA, Flyway, PostgreSQL (Neon)
**Frontend:** React, TypeScript, Vite, Tailwind CSS, React Router, Axios, react-hook-form + zod, Recharts
**Testes:** JUnit 5, Mockito, MockMvc, Testcontainers
**Deploy:** Render (backend, via Docker), Vercel (frontend) e Neon (banco)

## 📁 Estrutura

```
DinDin/
├── backend/    # API REST (Spring Boot)
└── frontend/   # Aplicação web (React)
```

## ⚙️ Como rodar

### Variáveis de ambiente

| Variável | Onde | Descrição |
|---|---|---|
| `DB_USERNAME` | Backend | Usuário do PostgreSQL |
| `DB_PASSWORD` | Backend | Senha do PostgreSQL |
| `JWT_SECRET` | Backend | Segredo para assinar os tokens (mínimo de 32 caracteres) |
| `CORS_ALLOWED_ORIGINS` | Backend (opcional) | Origens autorizadas, separadas por vírgula. Padrão: `http://localhost:5173` |
| `VITE_API_URL` | Frontend (opcional) | URL da API. Padrão: `http://localhost:8080` |

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

Sobe em `http://localhost:8080`. O esquema do banco é criado pelas migrations do Flyway na inicialização.

> Use o JDK 21. O Lombok falha em JDKs muito recentes (como o 27); confira com `./mvnw -v`.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Abre em `http://localhost:5173`.

### Testes

```bash
cd backend
./mvnw test
```

Os testes de integração usam um PostgreSQL em container (Testcontainers), então precisam do **Docker** rodando. Eles não usam o banco real nem as variáveis acima.

## 🔌 API

Tudo, exceto `/auth/**`, exige o header `Authorization: Bearer <token>`.

| Rota | Métodos | Descrição |
|---|---|---|
| `/auth/register`, `/auth/login` | POST | Cria a conta e entra; retornam o token |
| `/categorias`, `/categorias/{id}` | GET, POST, PUT, DELETE | Categorias do usuário |
| `/transacoes?mes=AAAA-MM`, `/transacoes/{id}` | GET, POST, PUT, DELETE | Transações do usuário; a listagem é por mês |

Regras principais:

- O `tipo` da transação (`RECEITA` ou `DESPESA`) deve ser igual ao tipo da categoria.
- Dados de outro usuário nunca aparecem: o acesso retorna **404**.
- Sem token, ou com token inválido, a resposta é **401**.
- Uma categoria com transações não pode ser removida nem mudar de tipo (**409**).
- Senha do cadastro: mínimo de 8 caracteres, com 1 letra maiúscula e 1 número.

## ☁️ Deploy

- **Banco:** Neon
- **Backend:** Render, com Docker (`backend/Dockerfile`). Defina `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET` e `CORS_ALLOWED_ORIGINS` (o endereço do frontend).
- **Frontend:** Vercel, com a pasta raiz `frontend` e a variável `VITE_API_URL` apontando para o backend.

## 📄 Licença

MIT. Veja o arquivo [LICENSE](LICENSE).