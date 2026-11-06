package com.assistaplus.assista_plus_api.config;

import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    // Armazena um balde de tokens por IP de cliente
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    private Bucket createNewBucket() {
        return Bucket.builder()
                // Capacidade máxima de 15 tokens, reabastecendo 15 tokens a cada 1 minuto por IP
                .addLimit(limit -> limit.capacity(15).refillGreedy(15, Duration.ofMinutes(1)))
                .build();
    }

    private Bucket resolveBucket(String clientIp) {
        return cache.computeIfAbsent(clientIp, k -> createNewBucket());
    }

    // Método auxiliar para extrair o IP real considerando proxies (Render, Heroku, Nginx, etc.)
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            // O cabeçalho pode conter múltiplos IPs separados por vírgula (ex: cliente, proxy1, proxy2). O primeiro é o real.
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp.trim();
        }

        return request.getRemoteAddr();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Libera rotas do Swagger e documentação para não travar os testes visuais
        String path = request.getRequestURI();
        if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Pega o IP real do cliente (lidando com o proxy da Render)
        String clientIp = getClientIp(request);
        Bucket bucket = resolveBucket(clientIp);

        // Tenta consumir 1 token da bucket
        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            // Se estourou o limite (mais de 15 requisições por minuto), retorna 429
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(
                    "{\"erro\": \"Too Many Requests\", \"mensagem\": \"Limite de requisições excedido. Tente novamente mais tarde.\"}"
            );
        }
    }
}