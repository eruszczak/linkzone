package com.example.reddit.dto;

import com.example.reddit.model.Comment;
import com.example.reddit.model.Post;
import org.modelmapper.ModelMapper;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class CommentCreate {

    @NotBlank
    @NotNull
    private String content;

    public String getContent() {
        return content.trim();
    }

    public void setContent(String content) {
        this.content = content;
    }
}
