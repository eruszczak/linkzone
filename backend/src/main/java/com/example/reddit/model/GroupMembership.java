package com.example.reddit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "group_membership", uniqueConstraints = @UniqueConstraint(columnNames = {"group_id", "user_id"}))
public class GroupMembership extends DateAudit {

    @Id
    @GeneratedValue
    private Long id;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE)
    // https://stackoverflow.com/questions/13370221/jpa-hibernate-detached-entity-passed-to-persist
    @JoinColumn(name = "group_id")
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Group group;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE)
    // https://stackoverflow.com/questions/13370221/jpa-hibernate-detached-entity-passed-to-persist
    @JoinColumn(name = "user_id")
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Account account;

    public GroupMembership() {
    }

    public GroupMembership(Group group, Account account) {
        this.group = group;
        this.account = account;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
