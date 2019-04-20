package pl.reryk.linkzone.validation.validator;

import pl.reryk.linkzone.dto.Password;
import pl.reryk.linkzone.validation.annotation.PasswordMatches;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator
        implements ConstraintValidator<PasswordMatches, Password> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(Password account, ConstraintValidatorContext context) {
        // if passwords are not provided, validate successfully
        if (account.getPassword() == null && account.getPasswordConfirm() == null) {
            return true;
        }
        return account.getPassword() != null &&
                account.getPasswordConfirm() != null &&
                account.getPassword().equals(account.getPasswordConfirm());
    }
}