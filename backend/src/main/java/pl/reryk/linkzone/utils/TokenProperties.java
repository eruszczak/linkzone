package pl.reryk.linkzone.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "security.jwt")
public class TokenProperties {

    private long expirationInMs;
    private String secret;

    public TokenProperties(long expirationInMs, String secret) {
        this.expirationInMs = expirationInMs;
        this.secret = secret;
    }

    public TokenProperties() {
    }

    public long getExpirationInMs() {
        return expirationInMs;
    }

    public void setExpirationInMs(long expirationInMs) {
        this.expirationInMs = expirationInMs;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}