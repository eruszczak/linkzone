package com.example.reddit.validation;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

public class ErrorResponse {

    private final String title;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> errors = new ArrayList<>();

    public ErrorResponse(String title) {
        this.title = title;
    }

    public void addError(String error) {
        errors.add(error);
    }

    public List<String> getErrors() {
        return errors;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "errors=" + errors +
                ", title='" + title + '\'' +
                '}';
    }
}