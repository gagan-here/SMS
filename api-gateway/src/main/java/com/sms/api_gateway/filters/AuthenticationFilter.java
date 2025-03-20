package com.sms.api_gateway.filters;

import com.sms.api_gateway.JwtService;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Slf4j
@Component
public class AuthenticationFilter
    extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

  private final JwtService jwtService;

  public AuthenticationFilter(JwtService jwtService) {
    super(Config.class);
    this.jwtService = jwtService;
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      log.info("Login request: {}", exchange.getRequest().getURI());

      String path = exchange.getRequest().getURI().getPath();

      // Skip authentication for public endpoints
      if (path.startsWith("/auth/")) {
        return chain.filter(exchange);
      }

      final String tokenHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

      if (tokenHeader == null || !tokenHeader.startsWith("Bearer")) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        log.error("Authorization token header not found");
        return exchange.getResponse().setComplete();
      }

      final String token = tokenHeader.split("Bearer ")[1];

      try {
        String userId = jwtService.getUserIdFromToken(token);
        String role = jwtService.getRoleFromToken(token).toUpperCase();

        // Role-based access control
        if (path.startsWith("/students/") && !"STUDENT".equals(role)) {
          log.error("Student role required for student endpoints");
          exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
          return exchange.getResponse().setComplete();
        }

        if (path.startsWith("/teachers/") && !"TEACHER".equals(role)) {
          log.error("Teacher role required for teacher endpoints");
          exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
          return exchange.getResponse().setComplete();
        }

        ServerWebExchange modifiedExchange =
            exchange
                .mutate()
                .request(r -> r.header("X-User-Id", userId).header("X-User-Role", role))
                .build();

        return chain.filter(modifiedExchange);
      } catch (JwtException e) {
        log.error("JWT validation error: {}", e.getMessage());
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
      }
    };
  }

  public static class Config {}
}
