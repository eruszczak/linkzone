package com.example.reddit.dto;

import com.example.reddit.controller.post.PostType;
import com.example.reddit.model.Group;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class GroupResponseShort {
    public String name;
    public String description;
    public String duration;
    public int subscribers;
    public boolean isSubbed;
    public long id;

    public AccountSummary creator;

    public String bannerUrl;

    public GroupResponseShort(Group group) {
        this.name = group.getName();
        this.description = group.getDescription();
        this.duration = "";
        this.subscribers = 14654;
        this.id = group.getId();
        this.creator = new AccountSummary(group.getCreator());
        this.bannerUrl = group.getBannerUrl();
    }

    public GroupResponseShort(Group group, boolean isSubbed) {
        this(group);
        this.isSubbed = isSubbed;
    }
}