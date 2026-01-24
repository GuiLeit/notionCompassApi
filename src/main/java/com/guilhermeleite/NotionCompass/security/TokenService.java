package com.guilhermeleite.NotionCompass.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.guilhermeleite.NotionCompass.domains.user.User;
import com.guilhermeleite.NotionCompass.dtos.AuthTokenDetailsDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    public String issuer = "NotionCompassApi";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration.hours}")
    private Integer expirationHours;

    public AuthTokenDetailsDto generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.secret);
            Instant expirationDate = this.getExpirationDate();

            String token = JWT.create()
                    .withIssuer(this.issuer)
                    .withSubject(user.getNotionUserId())
                    .withExpiresAt(expirationDate)
                    .sign(algorithm);

            return new AuthTokenDetailsDto(
                    token,
                    expirationDate
            );
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error generating JWT token", exception);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.secret);
            return JWT.require(algorithm)
                    .withIssuer(this.issuer)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return "";
        }
    }

    private Instant getExpirationDate() {
        return LocalDateTime.now().plusHours(this.expirationHours).toInstant(ZoneOffset.of("-03:00"));
    }
}
