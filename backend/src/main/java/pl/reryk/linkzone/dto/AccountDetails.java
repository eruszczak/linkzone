package pl.reryk.linkzone.dto;

import org.apache.commons.lang3.StringUtils;
import pl.reryk.linkzone.model.Account;

import java.time.Instant;

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
        if (StringUtils.isNotBlank(account.getAvatar())) {
            avatar = "https://linkzone.s3.eu-central-1.amazonaws.com/" + account.getAvatar();
        }
        createdAt = account.getCreatedAt();
        isAdmin = account.isAdmin();
    }
}
