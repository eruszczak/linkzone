package com.example.reddit.dto;

import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;

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
