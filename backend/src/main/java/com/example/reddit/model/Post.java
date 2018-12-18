package com.example.reddit.model;

import com.example.reddit.controller.post.PostType;
import com.example.reddit.utils.Utils;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "posts")
public class Post extends DateAudit {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @NotNull
    private Account account;

    @ManyToOne
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
//    private Set<Comment> comments = new HashSet<>();

    // group membership
    @NotNull
    private String title;

    @NotNull
    private String content;

    private String slug;

    @Enumerated(EnumType.ORDINAL)
    @NotNull
    private PostType postType;

    private boolean locked;

    public Post() {
    }

    @PrePersist
    @PreUpdate
    public void beforeSaveAndUpdate() {
        setSlug(Utils.getSlug(title));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", account=" + account +
                ", group=" + group +
                ", title='" + title + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    //    public void removeComment(PostComment comment) {
//        comments.remove(comment);
//        comment.setPost(null);
//    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof PostComment )) return false;
//        return id != null && id.equals(((PostComment) o).id);
//    }


    public PostType getPostType() {
        return postType;
    }

    public void setPostType(PostType postType) {
        this.postType = postType;
    }

    public boolean locked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
