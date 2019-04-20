package pl.reryk.linkzone.validation.annotation;

import pl.reryk.linkzone.validation.validator.NoSpacesValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NoSpacesValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoSpacesConstraint {
    String MESSAGE = "no_spaces_allowed";

    String message() default MESSAGE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
