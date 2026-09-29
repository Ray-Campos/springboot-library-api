# API de Gestão de Biblioteca

Uma API RESTful desenvolvida com Spring Boot para o gerenciamento completo de uma biblioteca. Este backend centraliza as regras de negócio, persistência de dados e controle de transações, servindo como a base para o sistema de gerenciamento.

> **Cliente CLI:** [dart-library-cli](https://github.com/Ray-Campos/dart-library-cli)

## Tecnologias Utilizadas

* **Linguagem:** Java
* **Framework:** Spring Boot (Spring Web, Spring Data JPA)
* **Banco de Dados:** PostgreSQL
* **Infraestrutura:** Docker (Containerização do Banco de Dados)
* **Mapeamento Objeto-Relacional:** Hibernate

## Arquitetura e Regras de Negócio

O sistema é construído focado no domínio, com entidades isoladas e regras transacionais rigorosas:

* **Usuários (Users):** Divididos por papéis (`STUDENT`, `PROFESSOR`, `EXTERNAL`), o que define limites de empréstimos ativos e prazos de devolução. Suporta bloqueios manuais ou automáticos (por multas ou danos).
* **Livros (Books):** O catálogo geral, armazenando as informações bibliográficas (Título, Autor, ISBN).
* **Cópias (Book Copies):** O acervo físico real. Um único livro no catálogo pode ter múltiplas cópias, cada uma com seu próprio status (`AVAILABLE`, `BORROWED`, `LOST`, `DAMAGED`).
* **Empréstimos (Loans):** Entidade puramente transacional. Os endpoints validam bloqueios, verificam a disponibilidade de cópias físicas, calculam multas por atraso e registram o histórico de circulação sem permitir a exclusão direta (DELETE) para preservar a integridade dos dados históricos.

## Pré-requisitos

* Java 17 ou superior.
* Maven.
* Docker e Docker Compose.

## Configuração e Execução

Para evitar conflitos com instalações locais do banco de dados na sua máquina, a aplicação está configurada para rodar em portas alternativas. O servidor Tomcat rodará na porta `8081` e o PostgreSQL via Docker será mapeado para a porta `5433`.

1. **Clone o repositório:**
```bash
git clone <https://github.com/Ray-Campos/springboot-library-api>
cd <library-api>

```


2. **Inicie o banco de dados via Docker:**
Isso fará o download da imagem do PostgreSQL e iniciará o container em segundo plano.
```bash
docker compose up -d

```


3. **Execute a aplicação Spring Boot:**
```bash
./mvnw spring-boot:run

```



A API estará disponível em `http://localhost:8081`. O Hibernate (configurado como `update`) criará automaticamente as tabelas no banco de dados na primeira execução.

## Estrutura de Endpoints

### Gestão de Usuários (`/api/users`)

* `GET /api/users` - Lista todos os usuários.
* `GET /api/users/{id}` - Busca um usuário específico.
* `POST /api/users` - Cria um novo usuário.
* `PUT /api/users/{id}` - Atualiza dados ou status de bloqueio.
* `DELETE /api/users/{id}` - Exclui um usuário.

### Catálogo de Livros (`/api/books`)

* `GET /api/books` - Lista todos os livros.
* `GET /api/books/{id}` - Busca um livro específico.
* `POST /api/books` - Cadastra um novo título.
* `PUT /api/books/{id}` - Atualiza informações do livro.
* `DELETE /api/books/{id}` - Exclui um título.

### Acervo Físico (`/api/copies`)

* `GET /api/copies` - Lista todas as cópias.
* `GET /api/copies/{id}` - Busca uma cópia específica.
* `POST /api/copies` - Registra uma nova cópia de um livro existente.
* `PUT /api/copies/{id}` - Atualiza o status de conservação da cópia.
* `DELETE /api/copies/{id}` - Remove uma cópia do sistema.

### Transações e Relatórios de Empréstimos (`/api/loans`)

**Operações Transacionais:**

* `POST /api/loans/borrow` - Realiza um empréstimo (valida regras de negócio e aloca uma cópia).
* `POST /api/loans/return` - Realiza a devolução (calcula multas e atualiza status da cópia/usuário).

**Consultas e Históricos (Apenas Leitura):**

* `GET /api/loans` - Retorna o histórico global da biblioteca.
* `GET /api/loans/active` - Retorna todos os empréstimos pendentes de devolução no sistema.
* `GET /api/loans/user/{userId}` - Retorna o histórico completo de um usuário.
* `GET /api/loans/user/{userId}/active` - Retorna apenas os empréstimos ativos de um usuário.
* `GET /api/loans/book/{bookId}` - Retorna o histórico de circulação completo de um livro.
* `GET /api/loans/book/{bookId}/active` - Retorna quais cópias de um livro estão emprestadas no momento.