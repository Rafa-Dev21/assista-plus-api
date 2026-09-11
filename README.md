# assista-plus-api
API REST desenvolvida em Java com Spring Boot para **gerenciamento e consulta de séries**.

O projeto tem como objetivo disponibilizar uma API capaz de cadastrar, consultar, atualizar e excluir informações relacionadas a séries, além de permitir o gerenciamento de temporadas, episódios, atores, diretores, gêneros, usuários e avaliações.

A API utiliza **Spring Data JPA** para a persistência dos dados e o **banco H2** durante o desenvolvimento. A aplicação também contará com validações dos dados, paginação das consultas, documentação dos endpoints através do Swagger/OpenAPI e implementação de HATEOAS para facilitar a navegação entre os recursos da API.

## Entidades

### Série

Representa a informação principal de uma série.

Uma série possui informações como título, sinopse, ano de lançamento e status.

Uma série pode possuir várias temporadas, participar de vários gêneros, possuir vários atores e receber várias avaliações.

### Temporada

Representa uma temporada pertencente a uma série.

Uma série pode possuir várias temporadas e cada temporada pertence a uma série.

### Episódio

Representa um episódio de uma temporada.

Uma temporada pode possuir vários episódios e cada episódio pertence a uma temporada.

### Ator

Representa um ator que participa de uma ou mais séries.

Uma série pode possuir vários atores e um ator pode participar de várias séries.

### Diretor

Representa o diretor responsável por uma ou mais séries.

Um diretor pode estar relacionado a várias séries.

### Gênero

Representa a categoria ou gênero de uma série, como Drama, Comédia, Ação, Ficção Científica, entre outros.

Uma série pode possuir vários gêneros e um gênero pode estar relacionado a várias séries.

### Usuário

Representa uma pessoa cadastrada na API.

O usuário poderá realizar avaliações das séries cadastradas no sistema.

### Avaliação

Representa a avaliação realizada por um usuário sobre uma série.

Uma avaliação possui uma nota e pode possuir um comentário. Cada avaliação está relacionada a um usuário e a uma série.

## Relacionamentos

O projeto utiliza diferentes tipos de relacionamentos entre as entidades:

* **One-to-Many (1:N):**

    * Série → Temporadas
    * Temporada → Episódios
    * Diretor → Séries
    * Série → Avaliações
    * Usuário → Avaliações

* **Many-to-Many (N:N):**

    * Série ↔ Atores
    * Série ↔ Gêneros

* **One-to-One (1:1):**

    * Será utilizado entre duas entidades do projeto para representar uma relação exclusiva entre seus registros.

## Funcionalidades

A API disponibilizará operações de:

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

## Tecnologias utilizadas

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

