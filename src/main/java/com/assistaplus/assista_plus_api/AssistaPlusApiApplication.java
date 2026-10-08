package com.assistaplus.assista_plus_api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Assista+ API",
				version = "1.0",
				description = "API REST para gerenciamento de séries, temporadas, episódios, atores, diretores, gêneros, usuários, avaliações e detalhes das séries."
		)
)
public class AssistaPlusApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssistaPlusApiApplication.class, args);
	}

}
