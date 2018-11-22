package com.example.reddit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ResourceLockedException extends RuntimeException {
    public ResourceLockedException(String value) {
        super("resource is locked: " + value);
    }
}
