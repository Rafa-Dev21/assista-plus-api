package com.assistaplus.assista_plus_api.config;

import com.assistaplus.assista_plus_api.security.ApiKeyInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private ApiKeyInterceptor apiKeyInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiKeyInterceptor)
                .addPathPatterns(
                        "/series/**",
                        "/temporadas/**",
                        "/episodios/**",
                        "/atores/**",
                        "/diretores/**"
                ) // Protege os módulos principais do sistema contra acesso sem chave de API válida
                .excludePathPatterns(
                        "/api/keys/**",
                        "/h2-console/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                );
    }
}