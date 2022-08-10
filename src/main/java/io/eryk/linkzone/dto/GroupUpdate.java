package io.eryk.linkzone.dto;

import io.eryk.linkzone.config.GroupConstants;
import io.eryk.linkzone.model.PostType;
import io.eryk.linkzone.validation.annotation.NoSpacesConstraint;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class GroupUpdate {

    @NoSpacesConstraint
    @NotNull
    @Size(min = GroupConstants.MIN_LENGTH, max = GroupConstants.MAX_LENGTH)
    public String name;

    public String description;

    @Size(min = 1)
    public List<PostType> postTypes;

    public List<Long> administrators;

    public List<Long> moderators;

    public List<String> tags;

    public boolean isDefault;

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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
