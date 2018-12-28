package com.example.reddit.dto;

import com.example.reddit.controller.post.PostType;
import com.example.reddit.model.Account;
import com.example.reddit.model.Group;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class GroupResponse {
    private long id;
    private String name;
    private String description;
    private Instant createdAt;
    private String bannerUrl;
    private String logo;

    private boolean isSubbed;
    private int subscribers;

    private List<AccountSummary> administrators;
    private List<AccountSummary> moderators;
    private List<PostType> postTypes;
    private List<String> tags;

    private AccountSummary creator;
    private boolean isModerator;
    private boolean isAdministrator;

    public GroupResponse(Group group) {
        this.name = group.getName();
        this.description = group.getDescription();
        this.subscribers = 14654;
        this.id = group.getId();
        this.creator = new AccountSummary(group.getCreator());
        this.administrators = group.getAdministrators().stream().map(AccountSummary::new).collect(Collectors.toList());
        this.moderators = group.getModerators().stream().map(AccountSummary::new).collect(Collectors.toList());
        this.postTypes = group.getPostTypes();
        this.tags = group.getTags();
        this.bannerUrl = group.getBannerUrl();
        this.logo = group.getLogo();
    }

    public GroupResponse(Group group, boolean isSubbed) {
        this(group);
        this.isSubbed = isSubbed;
    }

    public GroupResponse(Group group, boolean isSubbed, Account account) {
        this(group, isSubbed);
        isAdministrator = account != null && (group.getCreator().equals(account) || group.getAdministrators().contains(account));
        isModerator = account != null && (group.getModerators().contains(account) || isAdministrator);
    }

    public GroupResponse(IGroupResponseDto dto) {
        id = dto.getId();
        name = dto.getName();
        description = dto.getDescription();
        createdAt = dto.getCreatedAt();
        bannerUrl = dto.getBannerUrl();
        isSubbed = dto.getIsSubbed() > 0;
        subscribers = dto.getSubscribers();
        logo = dto.getLogo();
    }
}