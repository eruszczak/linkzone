package com.example.reddit.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "accounts")
public class Account extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    @Size(min = 3, max = 30)
    private String username;

    @NotBlank
    @Size(max = 40)
    @Email
    @Column(unique = true)
    private String email;

    private boolean isActive = true;

    private String tagline;

    private String avatar;

    @NotNull
    @Size(min = 60, max = 60)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "account_roles",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(mappedBy = "administrators")
    private Set<Group> administratedGroups = new HashSet<>();

    @ManyToMany(mappedBy = "moderators")
    private Set<Group> moderatedGroups = new HashSet<>();

    @OneToMany(mappedBy = "account")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "account")
    private List<Post> posts = new ArrayList<>();

    @PreRemove
    public void onDeleteSetCommentAccountAndPostAccountToNull() {
        for (Comment comment : comments) {
            comment.setAccount(null);
        }
        for (Post post : posts) {
            post.setAccount(null);
        }
    }

    public Account() { } // JPA only

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public List<GrantedAuthority> getAuthorities() {
        return getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Set<Group> getAdministratedGroups() {
        return administratedGroups;
    }

    public Set<Group> getModeratedGroups() {
        return moderatedGroups;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", isActive=" + isActive +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account that = (Account) o;
        return Objects.equals(id, that.id);
    }
}