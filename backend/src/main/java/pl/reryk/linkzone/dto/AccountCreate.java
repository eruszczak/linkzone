package pl.reryk.linkzone.dto;

import pl.reryk.linkzone.config.AccountConstants;
import pl.reryk.linkzone.validation.annotation.NoSpacesConstraint;
import pl.reryk.linkzone.validation.annotation.PasswordMatches;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@PasswordMatches
public class AccountCreate implements Password {

    @NoSpacesConstraint
    @Size(max = AccountConstants.USERNAME_MAX_LENGTH, min = AccountConstants.USERNAME_MIN_LENGTH,
            message = AccountConstants.INVALID_USERNAME)
    @NotNull
    private String username;

    @Size(max = AccountConstants.EMAIL_LENGTH)
    @Email
    private String email;

    @NotNull
    @Size(min = AccountConstants.PASSWORD_MIN_LENGTH, max = AccountConstants.PASSWORD_MAX_LENGTH)
    private String password;

    @NotNull
    @Size(min = AccountConstants.PASSWORD_MIN_LENGTH, max = AccountConstants.PASSWORD_MAX_LENGTH)
    private String passwordConfirm;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}