package io.eryk.linkzone.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CommentCreate {

    @NotBlank
    @NotNull
    @Size(max = 1000)
    private String content;

    public String getContent() {
        return content.trim();
    }

    public void setContent(String content) {
        this.content = content;
    }
}
