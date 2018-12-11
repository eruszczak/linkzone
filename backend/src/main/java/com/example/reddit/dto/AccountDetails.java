package com.example.reddit.dto;

import com.example.reddit.model.Account;

public class AccountDetails {

    private String username;

    private String email;

    private String tagline;

    private String avatar;
    
    public AccountDetails(Account account) {
        setEmail(account.getEmail());
        setUsername(account.getUsername());
        setTagline(account.getTagline());
        setAvatar(account.getAvatar());
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }
}
