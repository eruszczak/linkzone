package pl.reryk.linkzone.dto;

import pl.reryk.linkzone.validation.annotation.NoSpacesConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class LoginRequest {

    @NoSpacesConstraint
//    @Size(max = 20, min = 3, message = "{user.name.invalid}")
    @NotNull(message = "Please Enter your name")
    private String usernameOrEmail;

    @NotBlank
    private String password;

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}