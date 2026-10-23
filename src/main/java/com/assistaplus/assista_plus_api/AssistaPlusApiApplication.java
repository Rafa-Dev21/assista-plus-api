package com.assistaplus.assista_plus_api;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Assista+ API",
				version = "1.0.0",
				summary = "API REST para gerenciamento e consulta de séries de televisão",
				description = """
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

            # Paginação e Segurança

            Para otimizar o consumo de dados e proteger a infraestrutura do servidor, as consultas de listagem utilizam paginação indexada. Os parâmetros aceitam configurações personalizadas de página e tamanho, com uma trava de segurança estrita limitando o retorno a no máximo **50 registros por requisição**.

            #  Termos de Uso

            A utilização da **Assista+ API** está sujeita ao cumprimento das seguintes diretrizes de uso:
            * **Finalidade e Escopo:** Esta API é disponibilizada exclusivamente para fins acadêmicos, educacionais e de demonstração prática. Sua infraestrutura não deve ser empregada em ambientes de produção real.
            * **Política de Privacidade de Dados:** Fica estritamente proibido o envio, armazenamento ou manipulação de dados pessoais reais, confidenciais, chaves privadas ou informações sensíveis durante a execução dos testes.
            * **Responsabilidade Operacional:** Como o sistema utiliza um banco de dados em memória (H2), o usuário deve estar ciente de que as operações de criação, atualização e exclusão modificam o estado temporário do catálogo e serão resetadas a cada reinicialização da aplicação.

            # Como Utilizar a API

            * **Execução Local:** Os endpoints estão organizados por recursos funcionais. Utilize os exemplos pré-configurados e o recurso **Try it out** do Swagger UI para realizar requisições de teste em tempo real.
            * **Tratamento de Erros:** Antes de interagir com uma operação, certifique-se de que os registros associados (como IDs de chaves estrangeiras) já estejam previamente cadastrados para evitar falhas de validação.
            """,
				contact = @Contact(
						name = "Repositório do Projeto",
						url = "https://github.com/Rafa-Dev21/assista-plus-api"
				)
		),
		externalDocs = @ExternalDocumentation(
				description = "Contato",
				url = "https://github.com"
		)
)
public class AssistaPlusApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssistaPlusApiApplication.class, args);
	}
}
