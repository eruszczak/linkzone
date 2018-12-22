package com.example.reddit.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

public class ValidationErrorBuilder {

    public static ErrorResponse fromBindingErrors(Errors errors) {
        ErrorResponse errorResponse = new ErrorResponse("Validation failed - " + errors.getErrorCount() + " error(s)");
        for (final FieldError err : errors.getFieldErrors()) {
            errorResponse.addFieldError(err.getField(), err.getDefaultMessage());
        }
        for (final ObjectError err : errors.getGlobalErrors()) {
            errorResponse.addError(err.getObjectName() + ": " + err.getDefaultMessage());
        }
        return errorResponse;
    }

    public static ErrorResponse fromStrings(String name, String... errorMessages) {
        ErrorResponse errorResponse = new ErrorResponse(name);
        for (String err : errorMessages) {
            if (err != null) {
                errorResponse.addError(err);
            }
        }
        return errorResponse;
    }
}