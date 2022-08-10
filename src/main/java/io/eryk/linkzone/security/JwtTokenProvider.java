package io.eryk.linkzone.security;

import io.eryk.linkzone.model.Account;
import io.eryk.linkzone.utils.TokenProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    private final TokenProperties tokenProperties;

    @Autowired
    public JwtTokenProvider(TokenProperties tokenProperties) {
        this.tokenProperties = tokenProperties;
    }

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return generateToken(userPrincipal.getUsername(), userPrincipal.getId());
    }

    public String generateToken(Account account) {
        return generateToken(account.getUsername(), account.getId());
    }

    public String getUsernameFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getIssuer();
    }

    public Long getUserIdFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        SecretKey key = Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        log.debug("Validating token: {}", authToken);
        try {

            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);

            //OK, we can trust this JWT
            log.debug("Token is valid");
            return true;
        } catch (JwtException e) {
            //don't trust the JWT!
            log.error("Token not valid", e);
        }
        return false;
    }

    /*
        Helper method for generating security.jwt.secret
     */
    public String generateEncodedJwtSecret() {
        // HS256 is HMAC-SHA-256, and that produces digests that are 256 bits (32 bytes) long,
        // so HS256 requires that you use a secret key that is at least 32 bytes long.
        SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        return Encoders.BASE64.encode(secretKey.getEncoded());
    }

    private String generateToken(String username, Long userId) {
        SecretKey key = Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        log.info("Genrating token for {} (id={})", username, userId);
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tokenProperties.getExpirationInMs());
        return Jwts.builder()
                .setIssuer(username)
                .setIssuedAt(new Date())
                .setSubject(userId.toString())
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }
}