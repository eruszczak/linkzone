package io.eryk.linkzone.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super("bad_credentials");
    }

    public NotFoundException(Class klass, String value) {
        super("could not find: " + klass.getSimpleName() + ", value: " + value);
    }
}
