package pl.reryk.linkzone.model;

import pl.reryk.linkzone.config.AccountConstants;
import pl.reryk.linkzone.permissions.RoleName;
import lombok.Getter;
import lombok.Setter;
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
@Getter
@Setter
@Table(name = "accounts")
public class Account extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    @Size(min = AccountConstants.USERNAME_MIN_LENGTH, max = AccountConstants.USERNAME_MAX_LENGTH)
    private String username;

    @Size(max = AccountConstants.EMAIL_LENGTH)
    @Email
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
    private List<Group> administratedGroups = new ArrayList<>();

    @ManyToMany(mappedBy = "moderators")
    private List<Group> moderatedGroups = new ArrayList<>();

    @OneToMany(mappedBy = "account")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "account")
    private List<Post> posts = new ArrayList<>();

    public Account() {
    } // JPA only

    @PreRemove
    public void onDeleteSetCommentAccountAndPostAccountToNull() {
        for (Comment comment : comments) {
            comment.setAccount(null);
        }
        for (Post post : posts) {
            post.setAccount(null);
        }
    }

    public boolean isAdmin() {
        return getRoles().stream().map(Role::getName).anyMatch(roleName -> roleName == RoleName.ADMIN);
    }

    public List<GrantedAuthority> getAuthorities() {
        return getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
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