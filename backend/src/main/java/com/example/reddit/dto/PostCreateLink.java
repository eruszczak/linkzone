package com.example.reddit.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class PostCreateLink {

    @NotBlank
    private String title;

    @NotBlank
    @URL
    private String content;
}
