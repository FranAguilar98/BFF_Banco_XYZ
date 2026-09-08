package com.duocuc.bankbff.core.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utilidad JWT reutilizada por los 3 BFF. Cada canal (web/movil/cajero)
 * instancia este provider con su propia clave secreta, emisor ("issuer")
 * y tiempo de expiracion, definidos en el application.yml de cada modulo.
 * Esto aisla los tokens: un token emitido por el BFF Movil no es valido
 * en el BFF Cajero ni en el BFF Web, aunque compartan la misma libreria.
 */
public class JwtTokenProvider {

    private final SecretKey key;
    private final String issuer;
    private final long expirationMillis;

    public JwtTokenProvider(String secret, String issuer, long expirationMillis) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.issuer = issuer;
        this.expirationMillis = expirationMillis;
    }

    public String generateToken(String subject, List<String> roles) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMillis);
        return Jwts.builder()
                .subject(subject)
                .issuer(issuer)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(Claims claims) {
        Object roles = claims.get("roles");
        if (roles instanceof List<?>) {
            return ((List<?>) roles).stream().map(String::valueOf).collect(Collectors.toList());
        }
        return List.of();
    }

    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractSubject(String token) {
        return parseClaims(token).getSubject();
    }
}
