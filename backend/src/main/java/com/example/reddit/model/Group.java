package com.example.reddit.model;

import com.example.reddit.config.GroupConstants;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "group_tbl")
public class Group extends DateAudit {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Column(unique = true)
    @Size(min = GroupConstants.MIN_LENGTH, max = GroupConstants.MAX_LENGTH)
    private String name;

    @ManyToOne
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE) // todo, dont delete a group
    private Account creator;

    private String description;

    @Column(name = "banner_url")
    private String bannerUrl;

    @Column(name = "logo")
    private String logo;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Account> administrators = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Account> moderators = new ArrayList<>();

    @ElementCollection(targetClass = PostType.class)
    @JoinTable(name = "tbl_post_type", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "post_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private List<PostType> postTypes = new ArrayList<>(Arrays.asList(PostType.POST, PostType.LINK, PostType.MEDIA));

    @ElementCollection
    private List<String> tags = new ArrayList<>();

    @Column(name = "is_default")
    private boolean isDefault;

    public Group() {
    }

    public Group(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    //    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
//    @OneToMany(mappedBy = "group")
//    private Set<Post> posts = new HashSet<>();

//    public void addPost(Post post) {
//        this.posts.add(post);
//        post.setGroup(this);
//        System.out.println("after addPost");
//    }

//    public void removePost(Post post) {
//        this.posts.remove(post);
//        post.setGroup(null); // why, it will be deleted anyway
//    }


    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Account getCreator() {
        return creator;
    }

    public void setCreator(Account creator) {
        this.creator = creator;
    }

    public List<Account> getAdministrators() {
        return administrators;
    }

    public void setAdministrators(List<Account> administrators) {
        this.administrators = administrators;
    }

    public List<Account> getModerators() {
        return moderators;
    }

    public void setModerators(List<Account> moderators) {
        this.moderators = moderators;
    }

    public void addAdministrator(Account account) {
        this.administrators.add(account);
        account.getAdministratedGroups().add(this);
    }

    public void removeAdministrator(Account account) {
        this.administrators.remove(account);
        account.getAdministratedGroups().remove(this);
    }

    public void addModerator(Account account) {
        this.moderators.add(account);
        account.getModeratedGroups().add(this);
    }

    public void removeModerator(Account account) {
        this.moderators.remove(account);
        account.getModeratedGroups().remove(this);
    }

    public List<PostType> getPostTypes() {
        return postTypes;
    }

    public void setPostTypes(List<PostType> postTypes) {
        this.postTypes = postTypes;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", name='" + name + '\'' +
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
        Group that = (Group) o;
        return Objects.equals(id, that.id);
    }
}
