package pl.reryk.linkzone.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pl.reryk.linkzone.model.Account;
import pl.reryk.linkzone.utils.TokenProperties;

import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    private TokenProperties tokenProperties;

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
        Claims claims = Jwts.parser()
                .setSigningKey(tokenProperties.getSecret())
                .parseClaimsJws(token)
                .getBody();
        return claims.getIssuer();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(tokenProperties.getSecret())
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        log.info("Validating token: {}", authToken);
        try {
            Jwts.parser().setSigningKey(tokenProperties.getSecret()).parseClaimsJws(authToken);
            log.info("Token is valid");
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

    private String generateToken(String username, Long userId) {
        log.info("Genrating token for {} (id={})", username, userId);
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tokenProperties.getExpirationInMs());
        return Jwts.builder()
                .setIssuer(username)
                .setIssuedAt(new Date())
                .setSubject(userId.toString())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, tokenProperties.getSecret())
                .compact();
    }
}