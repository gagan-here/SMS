package com.sms.api_gateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  @Value("${jwt.secretKey}")
  private String jwtSecretKey;

  // Generate the secret key from the configured secret key string
  private SecretKey getSecretKey() {
    return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
  }

  // Extract the user ID (subject) from the token
  public String getUserIdFromToken(String token) {
    return getClaimFromToken(token, Claims::getSubject);
  }

  // Extract the role from the token
  public List<String> getRoleFromToken(String token) {
    return getClaimFromToken(token, claims -> claims.get("roles", List.class));
  }

  // Generic method to extract a claim from the token
  private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = getAllClaimsFromToken(token);
    return claimsResolver.apply(claims);
  }

  // Extract all claims from the token
  private Claims getAllClaimsFromToken(String token) {
    return Jwts.parser()
        .verifyWith(getSecretKey()) // Verify using the secret key
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  // Validate the token
  public boolean validateToken(String token) {
    try {
      Jwts.parser()
          .verifyWith(getSecretKey()) // Verify using the secret key
          .build()
          .parseSignedClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }
}
