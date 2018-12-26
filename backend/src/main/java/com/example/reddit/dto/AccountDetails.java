package com.example.reddit.dto;

import com.example.reddit.model.Account;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountDetails {

    private Long id;

    private String username;

    private String email;

    private String tagline;

    private String avatar;

    public AccountDetails(Account account) {
        setId(account.getId());
        setEmail(account.getEmail());
        setUsername(account.getUsername());
        setTagline(account.getTagline());
        setAvatar(account.getAvatar());
    }
}
