package com.example.reddit.dto;

import com.example.reddit.controller.post.PostType;
import com.example.reddit.model.Account;
import com.example.reddit.model.Group;

import java.util.List;
import java.util.stream.Collectors;

public class GroupResponse {
    public String name;
    public String description;
    public String duration;
    public int subscribers;
    public boolean isSubbed;
    public long id;
    public List<AccountSummary> administrators;
    public List<AccountSummary> moderators;
    public AccountSummary creator;
    public List<PostType> postTypes;
    public List<String> tags;
    public String bannerUrl;
    public boolean isModerator;
    public boolean isAdministrator;

    public GroupResponse(Group group) {
        this.name = group.getName();
        this.description = group.getDescription();
        this.duration = "";
        this.subscribers = 14654;
        this.id = group.getId();
        this.creator = new AccountSummary(group.getCreator());
        this.administrators = group.getAdministrators().stream().map(AccountSummary::new).collect(Collectors.toList());
        this.moderators = group.getModerators().stream().map(AccountSummary::new).collect(Collectors.toList());
        this.postTypes = group.getPostTypes();
        this.tags = group.getTags();
        this.bannerUrl = group.getBannerUrl();
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
}