package com.example.reddit.dto;

import com.example.reddit.config.GroupConstants;
import com.example.reddit.controller.post.PostType;
import com.example.reddit.validation.annotation.NoSpacesConstraint;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class GroupUpdate {

    @NoSpacesConstraint
    @NotNull
    @Size(min = GroupConstants.MIN_LENGTH, max = GroupConstants.MAX_LENGTH)
    private String name;

    private String description;

    @Size(min = 1)
    private List<PostType> postTypes;

    private List<Long> administrators;

    private List<Long> moderators;

    private List<String> tags;
}
