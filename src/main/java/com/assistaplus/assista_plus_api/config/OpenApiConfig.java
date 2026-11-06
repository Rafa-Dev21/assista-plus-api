package com.assistaplus.assista_plus_api.config;

import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;

@Configuration
public class OpenApiConfig {

    private static final String DESCRICAO_API = """
            # Sobre a API

            A **Assista+ API** é uma solução RESTful desenvolvida para centralizar, gerenciar e disponibilizar informações detalhadas sobre séries de televisão. Seu principal objetivo é permitir que aplicações clientes consumam um catálogo estruturado, facilitando a integração de dados entre sistemas.

            Por meio da plataforma, é possível administrar de forma completa o ciclo de vida de séries, temporadas, episódios, elencos de atores, diretores, gêneros cinematográficos, usuários e suas respectivas avaliações.

            # Funcionalidades e Recursos

            A API disponibiliza operações completas de criação, consulta, atualização e exclusão de registros (CRUD) para os seguintes módulos:
            * **Catálogo:** Séries, Temporadas, Episódios e Especificações Técnicas (Série Detalhes).
            * **Elenco e Produção:** Atores e Diretores.
            * **Interação:** Perfis de Usuários e Sistema de Avaliações com Notas e Críticas.

            # Tecnologias e Arquitetura

            O projeto foi construído utilizando práticas modernas de desenvolvimento de software:
            * **Spring Boot & Java 17:** Base robusta para a execução da aplicação.
            * **Spring Data JPA & Hibernate:** Persistência relacional com o banco de dados H2 em memória.
            * **Bean Validation:** Validação estática de campos na entrada de dados.
            * **Spring HATEOAS:** Implementação de hipermídia para garantir a navegabilidade entre os recursos através de links no formato JSON.

            # Paginação, Segurança, Rate Limiting e Idempotência

            Para otimizar o consumo de dados e proteger a infraestrutura do servidor, as consultas de listagem utilizam paginação indexada. Os parâmetros aceitam configurações personalizadas de página e tamanho, com uma trava de segurança estrita limitando o retorno a no máximo **50 registros por requisição**.

            Adicionalmente, a API conta com:
            * **Rate Limiting:** Mecanismo global baseado no endereço IP (*Token Bucket*), limitando a **15 requisições por minuto** por IP (retornando **429 Too Many Requests** em caso de excesso).
            * **Idempotência em Operações POST:** Proteção contra falhas de rede e retentativas duplicadas.

            # Mecanismo de Idempotência (`X-Idempotency-Key`)

            Todas as operações de cadastro (`POST`) suportam o envio opcional do cabeçalho **`X-Idempotency-Key`**:
            * **Retentativa Válida:** Se você enviar o mesmo `POST` com a mesma chave e exatamente os mesmos dados, a API reconhece que a requisição é idêntica e retorna a resposta anterior em cache instantaneamente (com o mesmo status de sucesso), evitando duplicidade na base.
            * **Dados Divergentes (Erro 422):** Se a mesma chave `X-Idempotency-Key` for reutilizada enviando dados diferentes no corpo da requisição, a API identifica a divergência de conteúdo e rejeita imediatamente a operação retornando o status **422 Unprocessable Entity**.

            **Dica para testes no Swagger:**
            * Você pode utilizar uma chave de teste padrão, como: `teste-idempotencia-123` (`a1b2c3d4-e5f6-7890-abcd-ef1234567890`).
            * Para testar o sucesso em cache, envie o mesmo payload duas vezes seguidas com a mesma chave.
            * Para testar a validação de segurança, envie a mesma chave alterando os dados do corpo para observar o retorno do erro `422`.

            # Versionamento da API

            A API suporta versionamento baseado em Header (`X-API-Version`), permitindo a evolução dos contratos sem quebrar clientes legados. As rotas específicas onde o versionamento se encontra ativo para testes são:
            * **`GET /series/buscar`**
            * **`GET /episodios/buscar`**
            * **`GET /temporadas/buscar`**

            **Como testar o Versionamento no Swagger:**
            1. Abra uma das rotas de busca listadas acima.
            2. Utilize o botão **Try it out**.
            3. Informe o parâmetro de busca e preencha o header opcional **`X-API-Version`** com o valor **`v1`** (retorno padrão em lista simples) ou **`v2`** (retorno envelopado contendo metadados e metrificações da consulta).

            # Repositório do Projeto

            Você pode acessar o código fonte completo e a documentação técnica da aplicação no GitHub:
            * **[Repositório no GitHub](https://github.com/Rafa-Dev21/assista-plus-api)**

            # Termos de Uso

            A utilização da **Assista+ API** está sujeita ao cumprimento das seguintes diretrizes de uso:
            * **Finalidade e Escopo:** Esta API é disponibilizada exclusivamente para fins acadêmicos, educacionais e de demonstração prática. Sua infraestrutura não deve ser empregada em ambientes de produção real.
            * **Política de Privacidade de Dados:** Fica estritamente proibido o envio, armazenamento ou manipulação de dados pessoais reais, confidenciais, chaves privadas ou informações sensíveis durante a execução dos testes.
            * **Responsabilidade Operacional:** Como o sistema utiliza um banco de dados em memória (H2), o usuário deve estar ciente de que as operações de criação, atualização e exclusão modificam o estado temporário do catálogo e serão resetadas a cada reinicialização da aplicação.
            """;

    @Bean
    public OperationCustomizer customizarHeaderIdempotencia() {
        return (operation, handlerMethod) -> {
            boolean isPost = handlerMethod.getMethod().isAnnotationPresent(PostMapping.class)
                    || handlerMethod.getBeanType().isAnnotationPresent(PostMapping.class);

            if (isPost) {
                if (operation.getParameters() == null) {
                    operation.setParameters(new ArrayList<>());
                }

                boolean jaExiste = operation.getParameters().stream()
                        .anyMatch(p -> "X-Idempotency-Key".equals(p.getName()));

                if (!jaExiste) {
                    Parameter idempotencyHeader = new Parameter()
                            .in("header")
                            .name("X-Idempotency-Key")
                            .description("Chave única (UUID) gerada pelo cliente para garantir idempotência em operações POST (Ex: a1b2c3d4-e5f6-7890-abcd-ef1234567890)")
                            .required(false)
                            .schema(new StringSchema());

                    operation.addParametersItem(idempotencyHeader);
                }
            }
            return operation;
        };
    }

    @Bean
    public GroupedOpenApi v1(OperationCustomizer customizarHeaderIdempotencia) {
        return GroupedOpenApi.builder()
                .group("v1")
                .pathsToMatch("/**")
                .addOperationCustomizer(customizarHeaderIdempotencia)
                .addOpenApiCustomizer(openApi -> openApi.info(
                        new io.swagger.v3.oas.models.info.Info()
                                .title("Assista+ API - v1")
                                .version("1.0.0")
                                .summary("API REST para gerenciamento e consulta de séries de televisão")
                                .description(DESCRICAO_API)
                ))
                .build();
    }

    @Bean
    public GroupedOpenApi v2(OperationCustomizer customizarHeaderIdempotencia) {
        return GroupedOpenApi.builder()
                .group("v2")
                .pathsToMatch("/**")
                .addOperationCustomizer(customizarHeaderIdempotencia)
                .addOpenApiCustomizer(openApi -> openApi.info(
                        new io.swagger.v3.oas.models.info.Info()
                                .title("Assista+ API - v2")
                                .version("2.0.0")
                                .summary("API REST para gerenciamento e consulta de séries de televisão")
                                .description(DESCRICAO_API)
                ))
                .build();
    }
}