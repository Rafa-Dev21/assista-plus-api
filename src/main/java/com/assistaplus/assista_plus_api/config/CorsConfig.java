package com.assistaplus.assista_plus_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Permite credenciais (cookies, headers de autorização, chaves de API, etc.)
        config.setAllowCredentials(true);

        // Libera origens (para desenvolvimento, permitimos "*" ou locais específicos)
        // Se quiser restringir, você pode colocar ex: "http://localhost:8081" (padrão do Expo)
        config.addAllowedOriginPattern("*");

        // Libera todos os headers necessários (incluindo os customizados que vamos usar na Parte 2)
        config.setAllowedHeaders(Arrays.asList(
                "Origin",
                "Content-Type",
                "Accept",
                "Authorization",
                "X-API-Key",
                "X-Idempotency-Key",
                "X-API-Version"
        ));

        // Libera os métodos HTTP que sua API utiliza
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}