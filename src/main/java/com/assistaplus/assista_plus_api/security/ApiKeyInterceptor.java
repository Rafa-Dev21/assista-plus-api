package com.assistaplus.assista_plus_api.security;

import com.assistaplus.assista_plus_api.model.ApiKey;
import com.assistaplus.assista_plus_api.repository.ApiKeyRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // 1. Libera rotas públicas, Swagger, H2 e o endpoint de chaves
        if (uri.startsWith("/swagger-ui") || uri.startsWith("/v3/api-docs") || uri.startsWith("/h2-console") || uri.startsWith("/api/keys")) {
            return true;
        }

        // 2. REGRA DO VERSIONAMENTO (Item 4.5):
        // Se o cliente enviar o header X-API-Version com "v1", a requisição passa livre (sem travas, sem chave)
        String versao = request.getHeader("X-API-Version");
        if ("v1".equalsIgnoreCase(versao)) {
            return true;
        }

        // 3. DAQUI PARA BAIXO É A V2 (OU PADRÃO): Exige a X-API-Key e aplica todas as travas de segurança
        String clientKey = request.getHeader("X-API-Key");

        if (clientKey == null || clientKey.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"erro\": \"Header X-API-Key ausente.\"}");
            return false;
        }

        Optional<ApiKey> apiKeyOpt = apiKeyRepository.findByKeyValue(clientKey);

        if (apiKeyOpt.isEmpty() || !apiKeyOpt.get().isActive()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"erro\": \"API Key inválida ou inativa.\"}");
            return false;
        }

        return true;
    }
}