package com.example.reddit.dto;

import com.example.reddit.config.GroupConstants;
import com.example.reddit.model.Account;
import com.example.reddit.model.Group;
import com.example.reddit.model.Post;
import com.example.reddit.validation.annotation.NoSpacesConstraint;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.URL;
import org.modelmapper.ModelMapper;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Optional;

public class PostCreateLink {

    @NotBlank
    private String title;

    private String link;

    public String getTitle() {
        return title.trim();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @NotBlank
    @URL
    public String getLink() {
        return (link != null &&
                !link.startsWith("http://") &&
                !link.startsWith("https://")) ?
                "http://" + link :
                link;
    }
    public void setLink(String content) {
        this.link = content;
    }
}
