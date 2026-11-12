package com.assistaplus.assista_plus_api.config;

import org.springdoc.webmvc.ui.SwaggerIndexPageTransformer;
import org.springdoc.webmvc.ui.SwaggerIndexTransformer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;

import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Configuration
public class SwaggerConfig {

    @Bean
    public SwaggerIndexTransformer customSwaggerIndexTransformer(
            SwaggerUiConfigProperties swaggerUiConfigProperties,
            SwaggerUiOAuthProperties swaggerUiOAuthProperties,
            SwaggerWelcomeCommon swaggerWelcomeCommon,
            ObjectMapperProvider objectMapperProvider) {

        return new SwaggerIndexPageTransformer(
                swaggerUiConfigProperties,
                swaggerUiOAuthProperties,
                swaggerWelcomeCommon,
                objectMapperProvider) {

            @Override
            public Resource transform(
                    HttpServletRequest request,
                    Resource resource,
                    ResourceTransformerChain transformerChain) throws IOException {

                Resource transformedResource = super.transform(request, resource, transformerChain);

                if (resource.getFilename() != null && resource.getFilename().equals("index.html")) {
                    String html = new BufferedReader(new InputStreamReader(transformedResource.getInputStream()))
                            .lines().collect(Collectors.joining("\n"));

                    // Ajustado aqui para ler o seu arquivo swagger-custom.css
                    String customCssTag = "<link rel=\"stylesheet\" type=\"text/css\" href=\"/swagger-custom.css\">";
                    html = html.replace("</head>", customCssTag + "</head>");

                    return new TransformedResource(transformedResource, html.getBytes());
                }

                return transformedResource;
            }
        };
    }
}
