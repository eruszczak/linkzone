package io.eryk.linkzone.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.eryk.linkzone.model.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
public class UserPrincipal implements UserDetails {
    private final Long id;

    private final String username;

    @JsonIgnore
    private final String email;

    @JsonIgnore
    private final String password;

    @JsonIgnore
    private Account account;

    private final Collection<? extends GrantedAuthority> authorities;

    private UserPrincipal(Long id, String username, String email, String password,
                          Collection<? extends GrantedAuthority> authorities, Account account) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.account = account;
    }

    public static UserPrincipal create(Account account) {
        List<GrantedAuthority> authorities = account.getAuthorities();
        log.debug("Creating UserPrincipal, username: {}, authorities: {}", account.getUsername(), authorities);
        return new UserPrincipal(
                account.getId(),
                account.getUsername(),
                account.getEmail(),
                account.getPassword(),
                authorities,
                account
        );
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPrincipal that = (UserPrincipal) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}