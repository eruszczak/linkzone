package com.example.reddit.dto;

import com.example.reddit.config.GroupConstants;
import com.example.reddit.model.Account;
import com.example.reddit.model.Group;
import com.example.reddit.validation.annotation.NoSpacesConstraint;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.modelmapper.ModelMapper;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class GroupCreate {

    @NoSpacesConstraint
    @NotNull
    @Size(min = GroupConstants.MIN_LENGTH, max = GroupConstants.MAX_LENGTH)
    private String name;

    private String description;

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
}
