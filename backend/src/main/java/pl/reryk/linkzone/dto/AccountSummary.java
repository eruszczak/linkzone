package pl.reryk.linkzone.dto;

import pl.reryk.linkzone.model.Account;

import java.time.Instant;

public class AccountSummary {

    private Long id;
    private String username;
    private Instant joined;
    private String tagline;

    public AccountSummary() {
    }

    public AccountSummary(Account account) {
        this.id = account.getId();
        this.username = account.getUsername();
        this.joined = account.getCreatedAt();
        this.tagline = account.getTagline();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Instant getJoined() {
        return joined;
    }

    public void setJoined(Instant joined) {
        this.joined = joined;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }
}
