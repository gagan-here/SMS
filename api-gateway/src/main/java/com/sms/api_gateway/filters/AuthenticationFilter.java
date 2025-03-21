package com.sms.api_gateway.filters;

import com.sms.api_gateway.JwtService;
import com.sms.api_gateway.enums.Roles;
import io.jsonwebtoken.JwtException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

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

      // Validate token integrity and expiration
      if (!jwtService.validateToken(token)) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        log.error("Invalid or expired token");
        return exchange.getResponse().setComplete();
      }

      try {
        HttpMethod requestType = exchange.getRequest().getMethod();
        List<String> role = jwtService.getRoleFromToken(token);

        Mono<Void> exchange1 = validateRoles(exchange, requestType, role, path);
        if (exchange1 != null) return exchange1;

        return chain.filter(exchange);
      } catch (JwtException e) {
        log.error("JWT validation error: {}", e.getMessage());
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
      }
    };
  }

  // Role-based access control
  private static Mono<Void> validateRoles(
      ServerWebExchange exchange, HttpMethod requestType, List<String> role, String path) {
    if (requestType.equals(HttpMethod.DELETE) && !role.contains(Roles.ADMIN.toString())) {
      log.error("Admin role required for DELETE endpoints");
      exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
      return exchange.getResponse().setComplete();
    }

    if (path.startsWith("/students") && !role.contains(Roles.STUDENT.toString())) {
      log.error("Student role required for student endpoints");
      exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
      return exchange.getResponse().setComplete();
    }

    if (path.startsWith("/teachers") && !role.contains(Roles.TEACHER.toString())) {
      log.error("Teacher role required for teacher endpoints");
      exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
      return exchange.getResponse().setComplete();
    }
    return null;
  }

  public static class Config {}
}
