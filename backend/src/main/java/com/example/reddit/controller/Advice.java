package com.example.reddit.controller;

import com.example.reddit.dto.UploadFileResponse;
import com.example.reddit.exception.*;
import com.example.reddit.validation.ErrorResponse;
import com.example.reddit.validation.ValidationErrorBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@ControllerAdvice
public class Advice {

    // these exceptions must be repeated in @ExceptionHandler below because this cannot be passed as its param.
    private static final Class[] exceptionClasses = {
            NotFoundException.class,
            UserNotFoundException.class,
            AlreadyExistsException.class,
            UsernameTakenException.class,
            EmailTakenException.class,
            NoPermissionsException.class,
            ResourceLockedException.class
    };

    @ExceptionHandler({
            NotFoundException.class,
            UserNotFoundException.class,
            AlreadyExistsException.class,
            UsernameTakenException.class,
            EmailTakenException.class,
            NoPermissionsException.class,
            ResourceLockedException.class,
            FileStorageException.class,
            MyFileNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleRegularExceptions(final Exception e)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        return createErrorResponse(e);
    }

    @ExceptionHandler(ValidationErrorException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(final ValidationErrorException e) {
        ErrorResponse errorResponse = ValidationErrorBuilder.fromBindingErrors(e.getErrors());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(final Exception exception)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        ErrorResponse errorResponse = new ErrorResponse(exception.getClass().getSimpleName());
        errorResponse.addError(exception.getMessage());
        // to get httpStatus from annotation, we need to know subclass of this exception
        for (Class klass: exceptionClasses) {
            if (klass.isInstance(exception)) {
                HttpStatus httpStatus = getResponseStatusAnnotationValue(klass);
                return new ResponseEntity<>(errorResponse, httpStatus);
            }
        }
        // return generic status code if subclass wasn't found
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private HttpStatus getResponseStatusAnnotationValue(final Class klass)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Annotation annotation = klass.getAnnotation(ResponseStatus.class);
        Class<? extends Annotation> type = annotation.annotationType();
        Method method = type.getMethod("value");
        return (HttpStatus) method.invoke(annotation);
    }
}
