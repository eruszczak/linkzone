package com.example.reddit.dto;

import com.example.reddit.config.GroupConstants;
import com.example.reddit.controller.post.PostType;
import com.example.reddit.validation.annotation.NoSpacesConstraint;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class GroupUpdate {

    @NoSpacesConstraint
    @NotNull
    @Size(min = GroupConstants.MIN_LENGTH, max = GroupConstants.MAX_LENGTH)
    private String name;

    private String description;

    @Size(min = 1)
    private List<PostType> postTypes;

    @Size(min = 1)
    private List<Long> administrators;

    private List<Long> moderators;

    private List<String> tags;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public List<PostType> getPostTypes() {
        return postTypes;
    }

    public void setPostTypes(List<PostType> postTypes) {
        this.postTypes = postTypes;
    }

    public List<Long> getAdministrators() {
        return administrators;
    }

    public void setAdministrators(List<Long> administrators) {
        this.administrators = administrators;
    }

    public List<Long> getModerators() {
        return moderators;
    }

    public void setModerators(List<Long> moderators) {
        this.moderators = moderators;
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
}
