package com.assistaplus.assista_plus_api.config;

import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

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
            * **Autenticação com Chave de API (`X-API-Key`):** Proteção obrigatória para garantir o acesso restrito aos módulos do catálogo na versão v2. 

            # Rotas Protegidas por Chave de API (`X-API-Key`)

            Na versão **v2**, o uso do cabeçalho **`X-API-Key`** é obrigatório para acessar os endpoints da aplicação:
            * **` /series/** `** (Gerenciamento e listagem protegida do catálogo de séries)
            * **` /temporadas/** `** (Gerenciamento de temporadas)
            * **` /episodios/** `** (Gerenciamento de episódios)
            * **` /atores/** `** (Gerenciamento de atores do elenco)
            * **` /diretores/** `** (Gerenciamento de diretores)

            *(Nota: O endpoint de gerenciamento de chaves `/api/keys`, as rotas públicas de documentação/console e todas as requisições realizadas na versão **v1** permanecem livres de autenticação e bloqueios).*

            # Mecanismo de Idempotência (`X-Idempotency-Key`)

            Todas as operações de cadastro (`POST`) na **v2** suportam o envio opcional do cabeçalho **`X-Idempotency-Key`**:
            * **Retentativa Válida:** Se você enviar o mesmo `POST` com a mesma chave e exatamente os mesmos dados, a API reconhece que a requisição é idêntica e retorna a resposta anterior em cache instantaneamente (com o mesmo status de sucesso), evitando duplicidade na base.
            * **Dados Divergentes (Erro 422):** Se a mesma chave `X-Idempotency-Key` for reutilizada enviando dados diferentes no corpo da requisição, a API identifica a divergência de conteúdo e rejeita imediatamente a operação retornando o status **422 Unprocessable Entity**.

            **Dica para testes no Swagger:**
            * Você pode utilizar uma chave de teste padrão, como: `teste-idempotencia-123` (`a1b2c3d4-e5f6-7890-abcd-ef1234567890`).
            * Para testar o sucesso em cache, envie o mesmo payload duas vezes seguidas com a mesma chave.
            * Para testar a validação de segurança, envie a mesma chave alterando os dados do corpo para observar o retorno do erro `422`.

            # Versionamento da API (`X-API-Version`)

            A API suporta versionamento global baseado no cabeçalho personalizado **`X-API-Version`**, permitindo demonstrar a evolução arquitetural do sistema sem quebrar clientes legados:
            * **Versão `v1` (Legado / Aberta):** Permite o consumo direto de todos os endpoints sem exigência de chave `X-API-Key`, sem bloqueios de *Rate Limiting* e sem verificação de idempotência.
            * **Versão `v2` (Moderna / Segura - Padrão):** Aplica todas as camadas de proteção da API, exigindo autenticação via `X-API-Key`, aplicando o limite de requisições por IP e validando chaves de idempotência em operações de escrita.

            **Como testar o Versionamento no Swagger:**
            1. Selecione a definição desejada (**v1** ou **v2**) no menu superior direito do Swagger (*Select a definition*).
            2. O cabeçalho **`X-API-Version`** será injetado automaticamente e travado com o valor correspondente à definição escolhida.
            3. Clique em **Try it out** e execute a requisição.

            # Repositório do Projeto

            Você pode acessar o código fonte completo e a documentação técnica da aplicação no GitHub:
            * **[Repositório no GitHub](https://github.com/Rafa-Dev21/assista-plus-api)**

            # Termos de Uso

            A utilização da **Assista+ API** está sujeita ao cumprimento das seguintes diretrizes de uso:
            * **Finalidade e Escopo:** Esta API é disponibilizada exclusivamente para fins acadêmicos, educacionais e de demonstração prática. Sua infraestrutura não deve ser empregada em ambientes de produção real.
            * **Política de Privacidade de Dados:** Fica estritamente proibido o envio, armazenamento ou manipulação de dados pessoais reais, confidenciais, chaves privadas ou informações sensíveis durante a execução dos testes.
            * **Responsabilidade Operacional:** Como o sistema utiliza um banco de dados em memória (H2), o usuário deve estar ciente de que as operações de criação, atualização e exclusão modificam o estado temporário do catálogo e serão resetadas a cada reinicialização da aplicação.
            """;

    private OperationCustomizer criarCustomizerPorVersao(String versao, boolean incluirTravasSeguranca) {
        return (operation, handlerMethod) -> {
            if (operation.getParameters() == null) {
                operation.setParameters(new ArrayList<>());
            }

            // 1. Injeta o header X-API-Version travado com a versão do grupo (v1 ou v2) usando enum de opções fixas
            operation.getParameters().removeIf(p -> "X-API-Version".equals(p.getName()));

            Parameter versionHeader = new Parameter()
                    .in("header")
                    .name("X-API-Version")
                    .description("Versão da API controlada pelo seletor de definição do Swagger")
                    .required(true) // Obrigatório para garantir que vai na requisição
                    .schema(new StringSchema()._default(versao)._enum(List.of(versao))); // Trava o valor com apenas a opção ativa

            operation.addParametersItem(versionHeader);

            // 2. Se for v2, injeta também X-Idempotency-Key e X-API-Key
            if (incluirTravasSeguranca) {
                boolean isPost = handlerMethod.getMethod().isAnnotationPresent(PostMapping.class)
                        || handlerMethod.getBeanType().isAnnotationPresent(PostMapping.class);

                if (isPost) {
                    boolean jaExisteIdempotency = operation.getParameters().stream()
                            .anyMatch(p -> "X-Idempotency-Key".equals(p.getName()));

                    if (!jaExisteIdempotency) {
                        Parameter idempotencyHeader = new Parameter()
                                .in("header")
                                .name("X-Idempotency-Key")
                                .description("Chave única (UUID) gerada pelo cliente para garantir idempotência em operações POST (Ex: a1b2c3d4-e5f6-7890-abcd-ef1234567890)")
                                .required(false)
                                .schema(new StringSchema());

                        operation.addParametersItem(idempotencyHeader);
                    }
                }

                String className = handlerMethod.getBeanType().getSimpleName();
                boolean isApiKeyController = className.contains("ApiKeyController");

                if (!isApiKeyController) {
                    boolean jaExisteApiKey = operation.getParameters().stream()
                            .anyMatch(p -> "X-API-Key".equals(p.getName()));

                    if (!jaExisteApiKey) {
                        Parameter apiKeyHeader = new Parameter()
                                .in("header")
                                .name("X-API-Key")
                                .description("Chave de API obrigatória para endpoints protegidos (Chave padrão: minha-chave-mestra-123)")
                                .required(false)
                                .schema(new StringSchema()._default("minha-chave-mestra-123"));

                        operation.addParametersItem(apiKeyHeader);
                    }
                }
            }

            return operation;
        };
    }

    @Bean
    public GroupedOpenApi v1() {
        return GroupedOpenApi.builder()
                .group("v1")
                .pathsToMatch("/**")
                .addOperationCustomizer(criarCustomizerPorVersao("v1", false))
                .addOpenApiCustomizer(openApi -> openApi.info(
                        new io.swagger.v3.oas.models.info.Info()
                                .title("Assista+ API - v1 (Legado / Aberta)")
                                .version("1.0.0")
                                .summary("API REST para gerenciamento e consulta de séries de televisão (Sem travas de segurança)")
                                .description(DESCRICAO_API)
                ))
                .build();
    }

    @Bean
    public GroupedOpenApi v2() {
        return GroupedOpenApi.builder()
                .group("v2")
                .pathsToMatch("/**")
                .addOperationCustomizer(criarCustomizerPorVersao("v2", true))
                .addOpenApiCustomizer(openApi -> openApi.info(
                        new io.swagger.v3.oas.models.info.Info()
                                .title("Assista+ API - v2 (Moderna / Segura)")
                                .version("2.0.0")
                                .summary("API REST completa com X-API-Key, Rate Limit e Idempotência")
                                .description(DESCRICAO_API)
                ))
                .build();
    }
}