package io.eryk.linkzone.validation;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ErrorResponse {

    private final String title;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> errors = new ArrayList<>();

    private Map<String, String> fieldErrors = new HashMap<>();

    public ErrorResponse(String title) {
        this.title = title;
    }

    public void addFieldError(String field, String value) {
        fieldErrors.put(field, value);
    }

    public void addError(String error) {
        errors.add(error);
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "errors=" + errors +
                ", title='" + title + '\'' +
                '}';
    }
}