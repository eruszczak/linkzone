package pl.reryk.linkzone.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AlreadyExistsException extends RuntimeException {

    public AlreadyExistsException(Class klass, String value) {
        super("value is taken. class: " + klass.getSimpleName() + ", value: " + value);
    }
}
