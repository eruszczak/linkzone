package pl.reryk.linkzone.dto;

import pl.reryk.linkzone.config.GroupConstants;
import pl.reryk.linkzone.validation.annotation.NoSpacesConstraint;

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
