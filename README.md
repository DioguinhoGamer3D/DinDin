# DinDin

Sistema de controle de gastos e finanças pessoais (web app / PWA). Cada usuário tem a sua conta, as suas categorias e as suas transações, com painel de resumo mensal.

## 🚀 Tecnologias

### Backend
- Java 21
- Spring Boot 4
- PostgreSQL (hospedado no [Neon](https://neon.tech))
- Spring Data JPA + Flyway (migrations)
- Spring Security + JWT (jjwt)
- Bean Validation
- Lombok
- Testes: JUnit 5, Mockito, MockMvc e [Testcontainers](https://testcontainers.com/)

### Frontend
- React + TypeScript
- Vite
- Tailwind CSS v4
- React Router
- Axios
- react-hook-form + zod (formulários e validação)
- Recharts (gráficos)
- Lucide React (ícones)

## 📁 Estrutura

```
DinDin/
├── backend/
│   └── src/
│       ├── main/java/com/diogo/dindin/
│       │   ├── config/        # segurança e CORS
│       │   ├── controller/    # endpoints REST
│       │   ├── dto/           # objetos de entrada e saída da API
│       │   ├── exception/     # exceções e tratamento global de erros
│       │   ├── model/         # entidades JPA
│       │   ├── repository/    # acesso ao banco
│       │   ├── security/      # filtro JWT
│       │   └── service/       # regras de negócio
│       ├── main/resources/db/migration/   # migrations do Flyway
│       └── test/              # testes automatizados
└── frontend/
    └── src/
        ├── components/   # componentes reutilizáveis (layout, modais, cartões...)
        ├── contexts/     # autenticação e avisos (toasts)
        ├── hooks/        # hooks personalizados
        ├── pages/        # telas
        ├── services/     # chamadas à API (Axios)
        ├── types/        # tipos do domínio
        └── utils/        # formatação e cálculos do painel
```

## ✅ Status do projeto

- [x] **Fase 1**: modelo de dados (entidades JPA, migrations Flyway, repositórios)
- [x] **Fase 2**: autenticação JWT (registro, login, rotas protegidas)
- [x] **Fase 3**: CRUD de categorias e transações
- [x] **Fase 4**: base do frontend (Vite, React, TypeScript, Tailwind, React Router)
- [x] **Fase 5**: login e registro integrados à API, com rotas protegidas
- [x] **Fase 6**: categorias, transações e dashboard integrados à API
- [ ] **Fase 7**: PWA e deploy

## ⚙️ Configuração

### Backend

O backend lê as seguintes variáveis de ambiente:

| Variável | Obrigatória | Descrição |
|---|---|---|
| `DB_USERNAME` | Sim | Usuário do banco PostgreSQL (Neon) |
| `DB_PASSWORD` | Sim | Senha do banco PostgreSQL (Neon) |
| `JWT_SECRET` | Sim | Chave secreta usada para assinar os tokens JWT (pelo menos 32 caracteres) |
| `CORS_ALLOWED_ORIGINS` | Não | Origens do frontend autorizadas a chamar a API, separadas por vírgula. Padrão: `http://localhost:5173` |

A URL do banco está em `backend/src/main/resources/application.yml`.

No IntelliJ, configure as variáveis em **Run/Debug Configurations → Application → Environment variables**.

> **Sobre o JDK:** o projeto usa Java 21. O Lombok não funciona com JDKs muito recentes (a compilação falha, por exemplo, no Java 27). Se rodar o Maven pelo terminal, confira com `./mvnw -v` qual JDK ele está usando e ajuste o `JAVA_HOME` se for preciso.

### Frontend

| Variável | Obrigatória | Descrição |
|---|---|---|
| `VITE_API_URL` | Não | URL da API. Padrão: `http://localhost:8080` |

Requer o Node.js LTS.

## ▶️ Como rodar

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`. As migrations do Flyway são aplicadas automaticamente na inicialização.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

O app abre em `http://localhost:5173`. Outros comandos:

```bash
npm run build   # checa os tipos e gera a versão de produção
npm run lint    # análise do código com ESLint
```

### Testes do backend

```bash
cd backend
./mvnw test
```

Os testes de repositório e de integração usam um PostgreSQL em container (Testcontainers) e exigem o **Docker Desktop** rodando. Eles **não** usam o banco do Neon e não precisam das variáveis de ambiente: o `application-test.yml` define um segredo JWT só para testes.

A suíte cobre:
- serviços e validação dos DTOs (com Mockito);
- controllers (`@WebMvcTest`), incluindo segurança, CORS e códigos de erro;
- repositórios, com o banco real e as migrations aplicadas;
- isolamento entre usuários, de ponta a ponta (HTTP → filtro JWT → serviço → banco).

## 🗄️ Banco de dados

Tabelas: `users`, `categorias` e `transacoes`. O esquema é controlado só pelas migrations do Flyway, e o Hibernate apenas valida (`ddl-auto: validate`).

| Migration | O que faz |
|---|---|
| `V1__create_tables.sql` | Cria as tabelas, restrições e índices |
| `V2__add_cor_categorias.sql` | Adiciona a cor (`#RRGGBB`) às categorias |
| `V3__add_observacoes_transacoes.sql` | Adiciona as observações (opcionais) às transações |

> Uma migration já aplicada nunca deve ser editada (o Flyway guarda o checksum de cada uma). Mudanças no banco sempre entram como uma migration nova.

## 🔐 Autenticação

| Método | Rota | Descrição | Respostas |
|---|---|---|---|
| POST | `/auth/register` | Cria um novo usuário e retorna um token JWT | 201, 400, 409 (e-mail já cadastrado) |
| POST | `/auth/login` | Autentica um usuário existente e retorna um token JWT | 200, 400, 401 (credenciais inválidas) |

Todas as demais rotas exigem o header:

```
Authorization: Bearer <token>
```

Sem token, ou com token inválido ou expirado, a resposta é **401**. O token vale por 24 horas e carrega o e-mail, o id e o nome do usuário. O `user_id` usado nas consultas vem sempre do token, nunca do corpo da requisição.

**Regras de senha (cadastro):** mínimo de 8 caracteres, pelo menos 1 letra maiúscula e 1 número.

## 📚 Endpoints

Todas as rotas abaixo exigem o header `Authorization: Bearer <token>`.
Dados de outro usuário nunca são expostos: o acesso a um recurso alheio retorna **404**.

### Categorias

| Método | Rota | Descrição | Respostas |
|---|---|---|---|
| GET | `/categorias` | Lista as categorias do usuário | 200 |
| POST | `/categorias` | Cria uma categoria | 201, 400 |
| PUT | `/categorias/{id}` | Atualiza uma categoria | 200, 400, 404, 409 (tipo alterado com transações vinculadas) |
| DELETE | `/categorias/{id}` | Remove uma categoria | 204, 404, 409 (em uso) |

Corpo de criação e atualização:

```json
{ "nome": "Alimentação", "tipo": "DESPESA", "cor": "#EF4444" }
```

**Regras:** `nome` é obrigatório (até 100 caracteres); `tipo` é `RECEITA` ou `DESPESA`; `cor` é obrigatória, no formato `#RRGGBB`. Uma categoria com transações vinculadas não pode ser removida nem ter o tipo alterado (409), mas pode ser renomeada e recolorida.

### Transações

| Método | Rota | Descrição | Respostas |
|---|---|---|---|
| GET | `/transacoes?mes=AAAA-MM` | Lista as transações do mês, mais recentes primeiro | 200, 400 |
| POST | `/transacoes` | Cria uma transação | 201, 400, 404 |
| PUT | `/transacoes/{id}` | Atualiza uma transação | 200, 400, 404 |
| DELETE | `/transacoes/{id}` | Remove uma transação | 204, 404 |

Corpo de criação e atualização:

```json
{
  "descricao": "Mercado",
  "valor": 89.90,
  "data": "2026-11-12",
  "tipo": "DESPESA",
  "categoriaId": "<id da categoria>",
  "observacoes": "Compras do mês"
}
```

**Regras:**
- `descricao` é obrigatória (até 255 caracteres);
- `valor` deve ser positivo, com até 10 dígitos inteiros e 2 casas decimais;
- `data` no formato `AAAA-MM-DD`;
- o `tipo` da transação deve ser igual ao tipo da categoria (400 se diferir);
- a categoria deve pertencer ao usuário (404 se não);
- `observacoes` é opcional (até 500 caracteres);
- o parâmetro `mes` da listagem é obrigatório.

### Formato dos erros

```json
{
  "timestamp": "2026-11-12T20:15:30Z",
  "status": 404,
  "erro": "Not Found",
  "mensagem": "Categoria não encontrado(a)"
}
```

## 🖥️ Telas do frontend

| Rota | Tela | O que faz |
|---|---|---|
| `/login` | Entrar | Login com validação nos campos e mensagem de erro da API |
| `/registro` | Criar Conta | Cadastro com as mesmas regras de senha do backend |
| `/dashboard` | Dashboard | Saldo, entradas e saídas do mês, taxa de poupança, gráfico semanal, despesas por categoria e últimas movimentações |
| `/transacoes` | Transações | Lista do mês com busca e filtros por categoria e tipo, criação, edição e exclusão (com confirmação) |
| `/categorias` | Categorias | Criação, edição e exclusão de categorias com cor |

- Todas as telas, exceto login e registro, exigem estar autenticado; sem login, o app redireciona para `/login`.
- O token fica no `localStorage` (chave `dindin_token`). Se a API responder 401 numa rota protegida, a sessão é encerrada e o usuário volta ao login.
- Dashboard e Transações trabalham sempre com **um mês por vez**, escolhido no seletor de mês.
- O layout tem sidebar no desktop e menu recolhível no celular.

## 📄 Licença

Distribuído sob a licença MIT. Veja o arquivo [LICENSE](LICENSE).