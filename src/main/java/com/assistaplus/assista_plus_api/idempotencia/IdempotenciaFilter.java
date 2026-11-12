package com.assistaplus.assista_plus_api.idempotencia;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Component
public class IdempotenciaFilter extends OncePerRequestFilter {

    @Autowired
    private IdempotenciaRepository idempotenciaRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // REGRA DE VERSIONAMENTO: Se a versão for v1, o filtro de idempotência é ignorado completamente
        String versao = request.getHeader("X-API-Version");
        if ("v1".equalsIgnoreCase(versao)) {
            filterChain.doFilter(request, response);
            return;
        }

        String metodo = request.getMethod();
        String idempotencyKey = request.getHeader("X-Idempotency-Key");

        if ("POST".equalsIgnoreCase(metodo) && idempotencyKey != null && !idempotencyKey.isBlank()) {

            // Envolve o request para cachear o body de forma segura para o Spring ler múltiplas vezes
            CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(request);
            ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

            // Lê o corpo do wrapper (isso não consome o stream definitivo pois está cacheado)
            byte[] requestBodyBytes = wrappedRequest.getInputStream().readAllBytes();
            String requestBody = new String(requestBodyBytes, StandardCharsets.UTF_8);
            String currentHash = calcularHash(requestBody);

            Optional<IdempotenciaRegistro> registroExistente = idempotenciaRepository.findById(idempotencyKey);

            if (registroExistente.isPresent()) {
                IdempotenciaRegistro registro = registroExistente.get();

                // 1. Mesma chave, payload diferente -> 422 Unprocessable Entity
                if (registro.getRequestHash() == null || !registro.getRequestHash().equals(currentHash)) {
                    responseWrapper.setStatus(422);
                    responseWrapper.setContentType("application/json;charset=UTF-8");
                    responseWrapper.getWriter().write("{\"erro\": \"Chave de idempotência já foi utilizada com um payload diferente.\"}");
                    responseWrapper.getWriter().flush();
                    responseWrapper.copyBodyToResponse();
                    return;
                }

                // 2. Mesma chave e mesmo payload -> Retorna cache anterior
                responseWrapper.setStatus(registro.getStatusHttp());
                responseWrapper.setContentType("application/json;charset=UTF-8");
                responseWrapper.getWriter().write(registro.getRespostaJson());
                responseWrapper.getWriter().flush();
                responseWrapper.copyBodyToResponse();
                return;
            }

            // 3. Se é chave nova, executa o fluxo normal repassando o request com o body preservado
            try {
                filterChain.doFilter(wrappedRequest, responseWrapper);
            } finally {
                int statusRetornado = responseWrapper.getStatus();
                if (statusRetornado >= 200 && statusRetornado < 300) {
                    byte[] content = responseWrapper.getContentAsByteArray();
                    String respostaJson = new String(content, StandardCharsets.UTF_8);

                    IdempotenciaRegistro novoRegistro = new IdempotenciaRegistro();
                    novoRegistro.setChave(idempotencyKey);
                    novoRegistro.setStatusHttp(statusRetornado);
                    novoRegistro.setRespostaJson(respostaJson);
                    novoRegistro.setRequestHash(currentHash);

                    idempotenciaRepository.save(novoRegistro);
                }
                responseWrapper.copyBodyToResponse();
            }
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String calcularHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao calcular hash de idempotência", e);
        }
    }

    // Helper para manter o stream do body aberto e legível para o @RequestBody do Spring
    private static class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {
        private final byte[] cachedBody;

        public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
            super(request);
            this.cachedBody = request.getInputStream().readAllBytes();
        }

        @Override
        public ServletInputStream getInputStream() {
            ByteArrayInputStream bais = new ByteArrayInputStream(this.cachedBody);
            return new ServletInputStream() {
                @Override
                public boolean isFinished() {
                    return bais.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener listener) {}

                @Override
                public int read() {
                    return bais.read();
                }
            };
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
        }
    }
}