package pl.reryk.linkzone.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ValidationException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ImageUploadException extends RuntimeException {

    public ImageUploadException(String message) {
        super(message);
    }
}
