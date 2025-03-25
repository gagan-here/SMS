package com.sms.auth_service.service;

import com.sms.auth_service.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  @Value("${jwt.secretKey}")
  private String jwtSecretKey;

  private SecretKey getSecretKey() {
    return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
  }

  public String generateAccessToken(User user) {
    return Jwts.builder()
        .subject(user.getId().toString())
        .claim("email", user.getEmail())
        .claim("roles", user.getRoles())
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 100))
        .signWith(getSecretKey())
        .compact();
  }
}
