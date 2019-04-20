package pl.reryk.linkzone.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class NoPermissionsException extends RuntimeException {

    public NoPermissionsException() {
        super("you are not authorized to perform this action");
    }
}
