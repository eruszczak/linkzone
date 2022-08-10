package io.eryk.linkzone.dto;

import javax.validation.constraints.NotBlank;
import java.util.Optional;

public class PostCreate {

    @NotBlank
    private String title;

    private String content;

    public String getTitle() {
        return title.trim();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return Optional.ofNullable(content).map(String::trim).orElse(null);
    }

    public void setContent(String content) {
        this.content = content;
    }
}
