package com.example.pvpengine.common;


import com.example.pvpengine.apiKey.ApiKeyService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-KEY";

    private static final Set<String> PUBLIC_PATH_PREFIXES = Set.of(
            "/api/v1/games",
            "/actuator"
    );

    private final ApiKeyService apiKeyService;
    private final ObjectMapper objectMapper;


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return PUBLIC_PATH_PREFIXES.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String rawKey = request.getHeader(API_KEY_HEADER);

        if(rawKey == null || rawKey.isBlank()) {
            writeUnauthorized(response , "Missing X-API-KEY header");
            return;
        }

        Optional<UUID> gameIdOpt = apiKeyService.resolveGameId(rawKey);

        if(gameIdOpt.isEmpty()){
            log.warn("Invalid or expired key attempt from IP={}" , request.getRemoteAddr());
            writeUnauthorized(response , "Invalid or expired key");
            return;
        }

        try{
            TenantContext.setGameId(gameIdOpt.get());
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private void writeUnauthorized(HttpServletResponse response , String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String , Object> body = Map.of(
                "success" , false,
                "message", message,
                "timestamp", LocalDateTime.now().toString()
        );
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
