package com.example.reddit.dto;

import com.example.reddit.model.Account;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class AccountDetails {

    private Long id;

    private String username;

    private String email;

    private String tagline;

    private String avatar;

    private Instant createdAt;

    public AccountDetails(Account account) {
        setId(account.getId());
        setEmail(account.getEmail());
        setUsername(account.getUsername());
        setTagline(account.getTagline());
        setAvatar(account.getAvatar());
        setCreatedAt(account.getCreatedAt());
    }
}
