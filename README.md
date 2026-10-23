# assista-plus-api

API REST desenvolvida em Java com Spring Boot para **gerenciamento e consulta de séries**.

O projeto tem como objetivo disponibilizar uma API capaz de cadastrar, consultar, atualizar e excluir informações relacionadas a séries, além de permitir o gerenciamento de temporadas, episódios, atores, diretores, gêneros, usuários, avaliações e detalhes das séries.

A API utiliza **Spring Data JPA** para a persistência dos dados e o **banco H2** durante o desenvolvimento. A aplicação também utiliza validações dos dados, paginação das consultas, consultas personalizadas, documentação dos endpoints através do Swagger/OpenAPI e implementação de HATEOAS para facilitar a navegação entre os recursos da API.

---

# Entidades

## Série

Representa a informação principal de uma série.

Uma série possui informações como título, descrição, ano de lançamento e status.

Uma série pode possuir várias temporadas, participar de vários gêneros, possuir vários atores e receber várias avaliações.

## Temporada

Representa uma temporada pertencente a uma série.

Uma série pode possuir várias temporadas e cada temporada pertence a uma série.

## Episódio

Representa um episódio de uma temporada.

Uma temporada pode possuir vários episódios e cada episódio pertence a uma temporada.

## Ator

Representa um ator cadastrado no sistema.

Um ator possui nome e nacionalidade e pode participar de uma ou mais séries.

Uma série pode possuir vários atores e um ator pode participar de várias séries.

## Diretor

Representa o diretor responsável por uma ou mais séries.

Um diretor possui nome e nacionalidade e pode estar relacionado a várias séries.

## Gênero

Representa a categoria ou gênero de uma série, como Drama, Comédia, Ação, Terror e Ficção Científica.

Uma série pode possuir vários gêneros e um gênero pode estar relacionado a várias séries.

## Usuário

Representa uma pessoa cadastrada na API.

Um usuário pode realizar avaliações das séries cadastradas no sistema.

## Avaliação

Representa a avaliação realizada por um usuário sobre uma série.

Uma avaliação possui uma nota e pode possuir um comentário. Cada avaliação está relacionada a um usuário e a uma série.

## SerieDetalhes

Representa informações complementares de uma série.

A entidade possui uma relação **One-to-One** com a série, representando informações adicionais relacionadas exclusivamente a uma série.

---

# Relacionamentos

O projeto utiliza diferentes tipos de relacionamentos entre as entidades.

### One-to-Many (1:N)

* Série → Temporadas
* Temporada → Episódios
* Diretor → Séries
* Série → Avaliações
* Usuário → Avaliações

### Many-to-Many (N:N)

* Série ↔ Atores
* Série ↔ Gêneros

### One-to-One (1:1)

* Série ↔ SerieDetalhes

---

# Funcionalidades

A API possui operações de:

* Cadastro de séries e demais recursos;
* Consulta de registros;
* Consulta de um registro específico por ID;
* Atualização de registros;
* Exclusão de registros;
* Busca personalizada por diferentes atributos;
* Paginação nas consultas de listagem;
* Validação dos dados recebidos;
* Relacionamento entre as entidades;
* Avaliação de séries pelos usuários;
* Navegação entre recursos utilizando HATEOAS;
* Documentação dos endpoints através do Swagger/OpenAPI.

---

# Tecnologias utilizadas

* Java 17+
* Spring Boot
* Spring Web
* Spring Data JPA
* Hibernate
* H2 Database
* Bean Validation
* Spring HATEOAS
* Maven
* Swagger/OpenAPI

---

# Como executar o projeto

Clone o repositório:

```bash
git clone https://github.com/Rafa-Dev21/assista-plus-api.git
```

Entre na pasta:

```bash
cd assista-plus-api
```

Execute a aplicação:

### Windows

```bash
mvnw.cmd spring-boot:run
```

### Linux/macOS

```bash
./mvnw spring-boot:run
```

A aplicação será executada em:

```text
http://localhost:8080
```

---

# Swagger / OpenAPI

A documentação dos endpoints pode ser acessada através do Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

A documentação OpenAPI em formato JSON está disponível em:

```text
http://localhost:8080/v3/api-docs
```

---

# Roteiro de testes

Os testes abaixo estão organizados em uma sequência para facilitar a execução da API e a apresentação do projeto.

**Importante:** como o banco utilizado é o H2 em memória, os IDs podem mudar quando a aplicação for reiniciada.

Por isso, durante os testes, utilize os IDs retornados pelos cadastros anteriores.

---

# 1. DIRETORES

## 1.1 Cadastrar diretor

### POST

```text
POST /diretores
```

### JSON

```json
{
  "nome": "Rafael",
  "nacionalidade": "Brasileiro"
}
```

### Resultado esperado

```text
200 OK
```

Anote o `id` retornado.

---

## 1.2 Listar diretores

### GET

```text
GET /diretores
```

Ou utilizando paginação:

```text
GET /diretores?page=0&size=10
```

### Resultado esperado

```text
200 OK
```

---

## 1.3 Buscar diretor por ID

### GET

```text
GET /diretores/1
```

### Resultado esperado

```text
200 OK
```

---

## 1.4 Buscar diretor por nome

### GET

```text
GET /diretores/buscar?nome=Rafael
```

### Resultado esperado

```text
200 OK
```

---

## 1.5 Atualizar diretor

### PUT

```text
PUT /diretores/1
```

### JSON

```json
{
  "nome": "Rafael de Oliveira",
  "nacionalidade": "Brasileiro"
}
```

### Resultado esperado

```text
200 OK
```

---

## 1.6 Excluir diretor

### DELETE

```text
DELETE /diretores/1
```

### Resultado esperado

```text
204 No Content
```

> Durante a apresentação, recomenda-se deixar a exclusão para o final do roteiro para não remover um diretor que ainda será utilizado nas relações.

---

# 2. GÊNEROS

## 2.1 Cadastrar gênero

### POST

```text
POST /generos
```

### JSON

```json
{
  "nome": "Terror"
}
```

### Resultado esperado

```text
200 OK
```

Anote o `id`.

---

## 2.2 Listar gêneros

### GET

```text
GET /generos?page=0&size=10
```

### Resultado esperado

```text
200 OK
```

---

## 2.3 Buscar gênero por ID

### GET

```text
GET /generos/1
```

### Resultado esperado

```text
200 OK
```

---

## 2.4 Buscar gênero por nome

### GET

```text
GET /generos/buscar?nome=Terror
```

### Resultado esperado

```text
200 OK
```

---

## 2.5 Atualizar gênero

### PUT

```text
PUT /generos/1
```

### JSON

```json
{
  "nome": "Terror e Suspense"
}
```

### Resultado esperado

```text
200 OK
```

---

## 2.6 Excluir gênero

### DELETE

```text
DELETE /generos/1
```

### Resultado esperado

```text
204 No Content
```

---

# 3. SÉRIES

## 3.1 Cadastrar série

### POST

```text
POST /series
```

### JSON

```json
{
  "titulo": "Stranger Things",
  "descricao": "Uma série de ficção científica e suspense.",
  "anoLancamento": 2016,
  "status": "EM_EXIBICAO",
  "diretor": {
    "id": 1
  },
  "generos": []
}
```

### Resultado esperado

```text
200 OK
```

Anote o `id` da série.

---

## 3.2 Listar séries

### GET

```text
GET /series?page=0&size=10
```

### Resultado esperado

```text
200 OK
```

---

## 3.3 Buscar série por ID

### GET

```text
GET /series/1
```

### Resultado esperado

```text
200 OK
```

Essa consulta também pode ser utilizada para demonstrar os links HATEOAS.

---

## 3.4 Buscar séries por título

### GET

```text
GET /series/buscar?titulo=Stranger
```

### Resultado esperado

```text
200 OK
```

---

## 3.5 Atualizar série

### PUT

```text
PUT /series/1
```

### JSON

```json
{
  "titulo": "Stranger Things",
  "descricao": "Uma série de ficção científica, suspense e mistério.",
  "anoLancamento": 2016,
  "status": "EM_EXIBICAO",
  "diretor": {
    "id": 1
  },
  "generos": [
    {
      "id": 1
    }
  ]
}
```

### Resultado esperado

```text
200 OK
```

---

## 3.6 Adicionar gênero à série

### PATCH

```text
PATCH /series/1/generos/1
```

Não é necessário enviar JSON.

### Resultado esperado

```text
200 OK
```

Depois consulte:

```text
GET /series/1
```

O gênero deverá aparecer relacionado à série.

---

## 3.7 Excluir série

### DELETE

```text
DELETE /series/1
```

### Resultado esperado

```text
204 No Content
```

> Realize esse teste somente depois dos demais testes relacionados à série.

---

# 4. ATORES

## 4.1 Cadastrar ator

### POST

```text
POST /atores
```

### JSON

```json
{
  "nome": "Millie Bobby Brown",
  "nacionalidade": "Britânica"
}
```

### Resultado esperado

```text
200 OK
```

Anote o `id`.

---

## 4.2 Listar atores

### GET

```text
GET /atores?page=0&size=10
```

### Resultado esperado

```text
200 OK
```

---

## 4.3 Buscar ator por ID

### GET

```text
GET /atores/1
```

### Resultado esperado

```text
200 OK
```

---

## 4.4 Buscar ator por nome

### GET

```text
GET /atores/buscar?nome=Millie
```

### Resultado esperado

```text
200 OK
```

---

## 4.5 Atualizar ator

### PUT

```text
PUT /atores/1
```

### JSON

```json
{
  "nome": "Millie Bobby Brown",
  "nacionalidade": "Britânica"
}
```

### Resultado esperado

```text
200 OK
```

---

## 4.6 Adicionar série ao ator

### PATCH

```text
PATCH /atores/1/series/1
```

Não é necessário enviar JSON.

### Resultado esperado

```text
200 OK
```

---

## 4.7 Excluir ator

### DELETE

```text
DELETE /atores/1
```

### Resultado esperado

```text
204 No Content
```

---

# 5. TEMPORADAS

## 5.1 Cadastrar temporada

### POST

```text
POST /temporadas
```

### JSON

```json
{
  "numero": 1,
  "serie": {
    "id": 1
  }
}
```

### Resultado esperado

```text
200 OK
```

Anote o `id`.

---

## 5.2 Listar temporadas

### GET

```text
GET /temporadas?page=0&size=10
```

### Resultado esperado

```text
200 OK
```

---

## 5.3 Buscar temporada por ID

### GET

```text
GET /temporadas/1
```

### Resultado esperado

```text
200 OK
```

---

## 5.4 Buscar temporada por número

### GET

```text
GET /temporadas/buscar?numero=1
```

### Resultado esperado

```text
200 OK
```

---

## 5.5 Atualizar temporada

### PUT

```text
PUT /temporadas/1
```

### JSON

```json
{
  "numero": 2,
  "serie": {
    "id": 1
  }
}
```

### Resultado esperado

```text
200 OK
```

---

## 5.6 Excluir temporada

### DELETE

```text
DELETE /temporadas/1
```

### Resultado esperado

```text
204 No Content
```

---

# 6. EPISÓDIOS

## 6.1 Cadastrar episódio

### POST

```text
POST /episodios
```

### JSON

```json
{
  "titulo": "Capítulo Um",
  "descricao": "O início da história.",
  "numero": 1,
  "duracao": 50,
  "temporada": {
    "id": 1
  }
}
```

### Resultado esperado

```text
200 OK
```

Anote o `id`.

---

## 6.2 Listar episódios

### GET

```text
GET /episodios?page=0&size=10
```

### Resultado esperado

```text
200 OK
```

---

## 6.3 Buscar episódio por ID

### GET

```text
GET /episodios/1
```

### Resultado esperado

```text
200 OK
```

---

## 6.4 Buscar episódio por título

### GET

```text
GET /episodios/buscar?titulo=Capítulo
```

### Resultado esperado

```text
200 OK
```

---

## 6.5 Atualizar episódio

### PUT

```text
PUT /episodios/1
```

### JSON

```json
{
  "titulo": "Capítulo Um - Atualizado",
  "descricao": "Primeiro episódio da temporada.",
  "numero": 1,
  "duracao": 52,
  "temporada": {
    "id": 1
  }
}
```

### Resultado esperado

```text
200 OK
```

---

## 6.6 Excluir episódio

### DELETE

```text
DELETE /episodios/1
```

### Resultado esperado

```text
204 No Content
```

---

# 7. SERIE DETALHES

## 7.1 Cadastrar detalhes

### POST

```text
POST /serie-detalhes
```

### JSON

```json
{
  "paisOrigem": "Estados Unidos",
  "idiomaOriginal": "Inglês",
  "classificacaoIndicativa": 16,
  "serie": {
    "id": 1
  }
}
```

### Resultado esperado

```text
201 Created
```

Anote o `id`.

---

## 7.2 Listar detalhes

### GET

```text
GET /serie-detalhes?page=0&size=10
```

### Resultado esperado

```text
200 OK
```

---

## 7.3 Buscar detalhes por ID

### GET

```text
GET /serie-detalhes/1
```

### Resultado esperado

```text
200 OK
```

---

## 7.4 Buscar detalhes por país

### GET

```text
GET /serie-detalhes/buscar?paisOrigem=Estados Unidos
```

### Resultado esperado

```text
200 OK
```

---

## 7.5 Atualizar detalhes

### PUT

```text
PUT /serie-detalhes/1
```

### JSON

```json
{
  "paisOrigem": "Estados Unidosss",
  "idiomaOriginal": "Inglês",
  "classificacaoIndicativa": 16,
  "serie": {
    "id": 1
  }
}
```

### Resultado esperado

```text
200 OK
```

---

## 7.6 Excluir detalhes

### DELETE

```text
DELETE /serie-detalhes/1
```

### Resultado esperado

```text
204 No Content
```

---

# 8. USUÁRIOS

## 8.1 Cadastrar usuário

### POST

```text
POST /usuarios
```

### JSON

```json
{
  "nome": "Rafael",
  "email": "rafael@example.com"
}
```

### Resultado esperado

```text
201 Created
```

Anote o `id`.

---

## 8.2 Listar usuários

### GET

```text
GET /usuarios?page=0&size=10
```

### Resultado esperado

```text
200 OK
```

---

## 8.3 Buscar usuário por ID

### GET

```text
GET /usuarios/1
```

### Resultado esperado

```text
200 OK
```

---

## 8.4 Buscar usuários por nome

### GET

```text
GET /usuarios/buscar?nome=Rafael
```

### Resultado esperado

```text
200 OK
```

---

## 8.5 Atualizar usuário

### PUT

```text
PUT /usuarios/1
```

### JSON

```json
{
  "nome": "Rafael de Oliveira",
  "email": "rafael@example.com"
}
```

### Resultado esperado

```text
200 OK
```

---

## 8.6 Excluir usuário

### DELETE

```text
DELETE /usuarios/1
```

### Resultado esperado

```text
204 No Content
```

---

# 9. AVALIAÇÕES

## 9.1 Cadastrar avaliação

### POST

```text
POST /avaliacoes
```

### JSON

```json
{
  "nota": 5,
  "comentario": "Excelente série!",
  "usuario": {
    "id": 1
  },
  "serie": {
    "id": 1
  }
}
```

### Resultado esperado

```text
201 Created
```

Anote o `id`.

---

## 9.2 Listar avaliações

### GET

```text
GET /avaliacoes?page=0&size=10
```

### Resultado esperado

```text
200 OK
```

---

## 9.3 Buscar avaliação por ID

### GET

```text
GET /avaliacoes/1
```

### Resultado esperado

```text
200 OK
```

---

## 9.4 Buscar avaliações por nota

### GET

```text
GET /avaliacoes/buscar?nota=5
```

### Resultado esperado

```text
200 OK
```

---

## 9.5 Atualizar avaliação

### PUT

```text
PUT /avaliacoes/1
```

### JSON

```json
{
  "nota": 4,
  "comentario": "Muito boa série!",
  "usuario": {
    "id": 1
  },
  "serie": {
    "id": 1
  }
}
```

### Resultado esperado

```text
200 OK
```

---

## 9.6 Excluir avaliação

### DELETE

```text
DELETE /avaliacoes/1
```

### Resultado esperado

```text
204 No Content
```

---

# 10. PAGINAÇÃO

Todos os endpoints de listagem utilizam paginação.

Exemplo:

```text
GET /series?page=0&size=10
```

Outros exemplos:

```text
GET /usuarios?page=0&size=10

GET /atores?page=0&size=10

GET /diretores?page=0&size=10

GET /generos?page=0&size=10

GET /temporadas?page=0&size=10

GET /episodios?page=0&size=10

GET /avaliacoes?page=0&size=10

GET /serie-detalhes?page=0&size=10
```

Os resultados são retornados de forma paginada e utilizam recursos do Spring HATEOAS.

---

# 11. HATEOAS

Os endpoints de consulta individual utilizam HATEOAS para facilitar a navegação entre os recursos.

Exemplo:

```text
GET /series/1
```

Exemplo de resposta:

```json
{
  "_links": {
    "self": {
      "href": "http://localhost:8080/series/1"
    },
    "atualizar": {
      "href": "http://localhost:8080/series/1"
    },
    "excluir": {
      "href": "http://localhost:8080/series/1"
    },
    "diretor": {
      "href": "http://localhost:8080/diretores/1"
    }
  },
  "id": 1,
  "titulo": "Stranger Things",
  "descricao": "Uma série de ficção científica e suspense.",
  "anoLancamento": 2016,
  "status": "EM_EXIBICAO"
}
```

Os links permitem acessar operações e recursos relacionados diretamente através da resposta da API.

---

# 12. VALIDAÇÃO

A API utiliza Bean Validation para validar os dados enviados nas requisições.

Exemplo de cadastro de série inválida:

### POST

```text
POST /series
```

### JSON

```json
{
  "titulo": "",
  "descricao": "",
  "anoLancamento": 1800,
  "status": "EM_EXIBICAO"
}
```

### Resultado esperado

```text
400 Bad Request
```

Exemplo de resposta:

```json
{
  "status": 400,
  "erro": "Erro de validação",
  "mensagens": {
    "titulo": "não deve estar em branco"
  }
}
```

---

# 13. ERRO 404

Também é possível demonstrar o comportamento quando um recurso não existe.

Exemplo:

```text
GET /series/999
```

### Resultado esperado

```text
404 Not Found
```

---

# 14. Códigos HTTP

A API utiliza códigos HTTP de acordo com o resultado da operação:

| Código | Significado                    |
| ------ | ------------------------------ |
| 200    | Operação realizada com sucesso |
| 201    | Recurso criado com sucesso     |
| 204    | Recurso excluído com sucesso   |
| 400    | Dados enviados são inválidos   |
| 404    | Recurso não encontrado         |
| 500    | Erro interno do servidor       |

---

# 15. Resumo dos endpoints

## Usuários

| Método | Endpoint           | Função            |
| ------ | ------------------ | ----------------- |
| GET    | `/usuarios`        | Listar usuários   |
| GET    | `/usuarios/{id}`   | Buscar usuário    |
| GET    | `/usuarios/buscar` | Buscar por nome   |
| POST   | `/usuarios`        | Cadastrar usuário |
| PUT    | `/usuarios/{id}`   | Atualizar usuário |
| DELETE | `/usuarios/{id}`   | Excluir usuário   |

## Séries

| Método | Endpoint                               | Função            |
| ------ | -------------------------------------- | ----------------- |
| GET    | `/series`                              | Listar séries     |
| GET    | `/series/{id}`                         | Buscar série      |
| GET    | `/series/buscar`                       | Buscar por título |
| POST   | `/series`                              | Cadastrar série   |
| PUT    | `/series/{id}`                         | Atualizar série   |
| DELETE | `/series/{id}`                         | Excluir série     |
| PATCH  | `/series/{serieId}/generos/{generoId}` | Associar gênero   |

## Atores

| Método | Endpoint                            | Função          |
| ------ | ----------------------------------- | --------------- |
| GET    | `/atores`                           | Listar atores   |
| GET    | `/atores/{id}`                      | Buscar ator     |
| GET    | `/atores/buscar`                    | Buscar por nome |
| POST   | `/atores`                           | Cadastrar ator  |
| PUT    | `/atores/{id}`                      | Atualizar ator  |
| DELETE | `/atores/{id}`                      | Excluir ator    |
| PATCH  | `/atores/{atorId}/series/{serieId}` | Associar série  |

## Diretores

| Método | Endpoint            | Função            |
| ------ | ------------------- | ----------------- |
| GET    | `/diretores`        | Listar diretores  |
| GET    | `/diretores/{id}`   | Buscar diretor    |
| GET    | `/diretores/buscar` | Buscar por nome   |
| POST   | `/diretores`        | Cadastrar diretor |
| PUT    | `/diretores/{id}`   | Atualizar diretor |
| DELETE | `/diretores/{id}`   | Excluir diretor   |

## Gêneros

| Método | Endpoint          | Função           |
| ------ | ----------------- | ---------------- |
| GET    | `/generos`        | Listar gêneros   |
| GET    | `/generos/{id}`   | Buscar gênero    |
| GET    | `/generos/buscar` | Buscar por nome  |
| POST   | `/generos`        | Cadastrar gênero |
| PUT    | `/generos/{id}`   | Atualizar gênero |
| DELETE | `/generos/{id}`   | Excluir gênero   |

## Temporadas

| Método | Endpoint             | Função              |
| ------ | -------------------- | ------------------- |
| GET    | `/temporadas`        | Listar temporadas   |
| GET    | `/temporadas/{id}`   | Buscar temporada    |
| GET    | `/temporadas/buscar` | Buscar por número   |
| POST   | `/temporadas`        | Cadastrar temporada |
| PUT    | `/temporadas/{id}`   | Atualizar temporada |
| DELETE | `/temporadas/{id}`   | Excluir temporada   |

## Episódios

| Método | Endpoint            | Função             |
| ------ | ------------------- | ------------------ |
| GET    | `/episodios`        | Listar episódios   |
| GET    | `/episodios/{id}`   | Buscar episódio    |
| GET    | `/episodios/buscar` | Buscar por título  |
| POST   | `/episodios`        | Cadastrar episódio |
| PUT    | `/episodios/{id}`   | Atualizar episódio |
| DELETE | `/episodios/{id}`   | Excluir episódio   |

## Avaliações

| Método | Endpoint             | Função              |
| ------ | -------------------- | ------------------- |
| GET    | `/avaliacoes`        | Listar avaliações   |
| GET    | `/avaliacoes/{id}`   | Buscar avaliação    |
| GET    | `/avaliacoes/buscar` | Buscar por nota     |
| POST   | `/avaliacoes`        | Cadastrar avaliação |
| PUT    | `/avaliacoes/{id}`   | Atualizar avaliação |
| DELETE | `/avaliacoes/{id}`   | Excluir avaliação   |

## Série Detalhes

| Método | Endpoint                 | Função             |
| ------ | ------------------------ | ------------------ |
| GET    | `/serie-detalhes`        | Listar detalhes    |
| GET    | `/serie-detalhes/{id}`   | Buscar detalhes    |
| GET    | `/serie-detalhes/buscar` | Buscar por país    |
| POST   | `/serie-detalhes`        | Cadastrar detalhes |
| PUT    | `/serie-detalhes/{id}`   | Atualizar detalhes |
| DELETE | `/serie-detalhes/{id}`   | Excluir detalhes   |

---

# Objetivo acadêmico

O projeto foi desenvolvido com o objetivo de aplicar conceitos de desenvolvimento de APIs REST utilizando Spring Boot, incluindo:

* Arquitetura REST;
* Spring Data JPA;
* Relacionamentos entre entidades;
* One-to-One;
* One-to-Many;
* Many-to-Many;
* Bean Validation;
* Enum;
* CRUD;
* Paginação;
* Consultas personalizadas;
* Spring HATEOAS;
* Swagger/OpenAPI;
* Códigos de status HTTP;
* Banco de dados H2.
