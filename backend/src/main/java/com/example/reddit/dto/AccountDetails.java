package com.example.reddit.dto;

import com.example.reddit.model.Account;
import com.example.reddit.model.Role;
import com.example.reddit.permissions.RoleName;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

public class AccountDetails {

    public Long id;

    public String username;

    public String email;

    public String tagline;

    public String avatar;

    public Instant createdAt;

    public boolean isAdmin;

    public AccountDetails(Account account) {
        id = account.getId();
        email = account.getEmail();
        username = account.getUsername();
        tagline = account.getTagline();
        avatar = account.getAvatar();
        createdAt = account.getCreatedAt();
        isAdmin = account.isAdmin();
    }
}
