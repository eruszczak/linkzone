package pl.reryk.linkzone;

import org.junit.Ignore;
import pl.reryk.linkzone.dto.AccountCreate;
import pl.reryk.linkzone.exception.EmailTakenException;
import pl.reryk.linkzone.exception.NoPermissionsException;
import pl.reryk.linkzone.exception.UserNotFoundException;
import pl.reryk.linkzone.exception.UsernameTakenException;
import pl.reryk.linkzone.model.Account;
import pl.reryk.linkzone.validation.annotation.NoSpacesConstraint;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
//@Ignore
public class AccountRestControllerTest extends Base {

    @Test
    public void accountNotFound() throws Exception {
        mockMvc.perform(get("/users" + "/notExistingUsername")
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void accountFound() throws Exception {
        mockMvc.perform(get("/users" + "/" + account.getUsername())
                .contentType(contentType))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteAccount() throws Exception {
        assertTrue(account.isActive());
        mockMvc.perform(delete("/users" + "/" + account.getUsername())
                .contentType(contentType)
                .headers(getAuthHeader(token)))
                .andDo(print())
                .andExpect(status().isOk());
        boolean thrown = false;
        try {
            accountService.findById(account.getId());
        } catch (UserNotFoundException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void emailMustBeUniqueIgnoreCase() throws Exception {
        JSONObject json = new JSONObject();
        String emailLowerCase = "testasd@gmail.com".toLowerCase();
        json.put(fieldUsername, "student");
        json.put(fieldPassword, "123123");
        json.put(fieldPasswordConfirm, "123123");
        json.put(fieldEmail, emailLowerCase);
        mockMvc.perform(post("/users" + "/")
                .contentType(contentType)
                .content(json.toString()))
                .andDo(print())
                .andExpect(status().isCreated());
        mockMvc.perform(post("/users" + "/")
                .contentType(contentType)
                .content(json.toString()))
                .andDo(print())
                .andExpect(status().isBadRequest());
        // ignore case
        json.put(fieldUsername, "differentUsername");
        String emailUpperCase = emailLowerCase.toUpperCase();
        json.put(fieldEmail, emailUpperCase);
        mockMvc.perform(post("/users" + "/")
                .contentType(contentType)
                .content(json.toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(new EmailTakenException(emailUpperCase).getMessage())));
    }

    @Test
    public void userCreateMustHaveUsernameEmailAndBothPasswords() throws Exception {
        JSONObject json = new JSONObject();
        mockMvc.perform(post("/users" + "/")
                .contentType(contentType)
                .content(json.toString()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void usernameMustBeUniqueIgnoreCases() throws Exception {
        JSONObject json = new JSONObject();
        String usernameLowerCase = "student".toLowerCase();
        json.put(fieldUsername, usernameLowerCase);
        json.put(fieldPassword, "123123");
        json.put(fieldPasswordConfirm, "123123");
        json.put(fieldEmail, "someemaildsa@gmail.com");
        mockMvc.perform(post("/users" + "/")
                .contentType(contentType)
                .content(json.toString()))
                .andDo(print())
                .andExpect(status().isCreated());
        mockMvc.perform(post("/users" + "/")
                .contentType(contentType)
                .content(json.toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(new UsernameTakenException(usernameLowerCase.toLowerCase()).getMessage())));
        // ignore case
        json.put(fieldUsername, usernameLowerCase.toUpperCase());
        mockMvc.perform(post("/users" + "/")
                .contentType(contentType)
                .content(json.toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(new UsernameTakenException(usernameLowerCase.toUpperCase()).getMessage())));
    }

    @Test
    public void usernameCannotContainSpaces() throws Exception {
        JSONObject json = new JSONObject();
        json.put(fieldUsername, "username with spaces");
        json.put(fieldPassword, "123123");
        json.put(fieldPasswordConfirm, "123123");
        json.put(fieldEmail, "te123123st@gmail.com");
        mockMvc.perform(post("/users" + "/")
                .contentType(contentType)
                .content(json.toString())
                .headers(getAuthHeader(token)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(NoSpacesConstraint.MESSAGE)));
    }

    @Test
    public void updatedUsernameMustContainNoSpaces() throws Exception {
        JSONObject json = new JSONObject();
        json.put(fieldUsername, "username withs paces");
        mockMvc.perform(put("/users" + "/" + account.getUsername())
                .contentType(contentType)
                .headers(getAuthHeader(token))
                .content(json.toString())
                .headers(getAuthHeader(token)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(NoSpacesConstraint.MESSAGE)));
    }

    @Test
    public void updatingUserWithoutATokenIsNotPossible() throws Exception {
        JSONObject json = new JSONObject();
        json.put(fieldUsername, account.getUsername());
        json.put(fieldEmail, account.getEmail());
        mockMvc.perform(put("/users" + "/" + account.getUsername())
                .contentType(contentType)
                .content(json.toString()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
//                .andExpect(content().string(containsString(JwtAuthenticationEntryPoint.MESSAGE)));
    }

    @Test
    public void updatingUserWithOtherUsersTokenIsNotPossible() throws Exception {
        AccountCreate accountCreate = new AccountCreate();
        accountCreate.setUsername("someuser");
        accountCreate.setEmail("someemail@gmail.com");
        accountCreate.setPassword("daseqwewq");
        accountCreate.setPasswordConfirm("daseqwewq");
        String someOtherUsersToken = jwtTokenProvider.generateToken(accountService.create(accountCreate));
        JSONObject json = new JSONObject();
        json.put(fieldUsername, account.getUsername());
        json.put(fieldEmail, account.getEmail());
        mockMvc.perform(put("/users" + "/" + account.getUsername())
                .contentType(contentType)
                .headers(getAuthHeader(someOtherUsersToken))
                .content(json.toString()))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString(new NoPermissionsException().getMessage())));
    }

    @Test
    public void canUpdateAccountWithoutChangingUsernameOrEmail() throws Exception {
        JSONObject json = new JSONObject();
        json.put(fieldUsername, account.getUsername());
        json.put(fieldEmail, account.getEmail());
        mockMvc.perform(put("/users" + "/" + account.getUsername())
                .contentType(contentType)
                .headers(getAuthHeader(token))
                .content(json.toString()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void accountCanBeUpdatedAndPasswordIsEncoded() throws Exception {
        JSONObject json = new JSONObject();
        String newUsername = "dasdasdqwe";
        String newEmail = "daseqwe123@gmail.com";
        String newPassword = "newdasdsaPassword";
        json.put(fieldUsername, newUsername);
        json.put(fieldEmail, newEmail);
        json.put(fieldPassword, newPassword);
        json.put(fieldPasswordConfirm, newPassword);
        mockMvc.perform(put("/users" + "/" + account.getUsername())
                .contentType(contentType)
                .content(json.toString())
                .headers(getAuthHeader(token)))
                .andExpect(status().isOk());
        // check if user with email or username doesn't exist
        boolean thrown = false;
        try {
            accountService.findByUsernameOrEmail(account.getUsername(), account.getEmail());
        } catch (UserNotFoundException e) {
            thrown = true;
        }
        assertTrue(thrown);
        Account refreshed = accountService.findByUsername(newUsername);
        // check if update date has changed and created date hasn't
        assertTrue(account.getCreatedAt().equals(refreshed.getCreatedAt()));
        assertTrue(account.getUpdatedAt().isBefore(refreshed.getUpdatedAt()));
        // check if new password is encoded
        assertFalse(passwordEncoder.matches(password, refreshed.getPassword()));
        assertTrue(passwordEncoder.matches(newPassword, refreshed.getPassword()));
    }

    @Test
    public void validTokenIsObtainedUsingUsername() throws Exception {
        JSONObject json = new JSONObject();
        json.put(fieldUsernameOrEmail, account.getUsername());
        json.put(fieldPassword, password);
        mockMvc.perform(post("/users" + "/login")
                .contentType(contentType)
                .content(json.toString()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void validTokenIsObtainedUsingEmail() throws Exception {
        JSONObject json = new JSONObject();
        json.put(fieldUsernameOrEmail, account.getEmail());
        json.put(fieldPassword, password);
        mockMvc.perform(post("/users" + "/login")
                .contentType(contentType)
                .content(json.toString()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void toObtainTokenMustProvideValidCredentials() throws Exception {
        JSONObject json = new JSONObject();
        json.put(fieldUsernameOrEmail, "not valid");
        json.put(fieldPassword, "not valid");
        mockMvc.perform(post("/users" + "/login")
                .contentType(contentType)
                .content(json.toString()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void relatedObjectsAreDeleted() throws Exception {
        // comments and posts should not be deleted
    }

    @Test
    public void validTokenButUserDoesNotExistAnymore() throws Exception {
        // remove account
        accountService.delete(account);
        JSONObject json = new JSONObject();
        json.put(fieldUsername, account.getUsername());
        json.put(fieldEmail, account.getEmail());
        // try to update account that does not exist
        boolean thrown = false;
        try {
            mockMvc.perform(put("/users" + "/" + account.getUsername())
                    .contentType(contentType)
                    .headers(getAuthHeader(token))
                    .content(json.toString()))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        } catch (UserNotFoundException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);

    }

    @Test
    public void newAccountIsCreatedAndPasswordIsEncoded() throws Exception {
        JSONObject json = new JSONObject();
        String username = "myuniqueusername";
        String password = "123123";
        json.put(fieldUsername, username);
        json.put(fieldPassword, password);
        json.put(fieldPasswordConfirm, password);
        json.put(fieldEmail, "te123123231312312321st@gmail.com");
        mockMvc.perform(post("/users" + "/")
                .contentType(contentType)
                .content(json.toString()))
                .andDo(print())
                .andExpect(status().isCreated());
        Account account = accountService.findByUsername(username);
        Assert.assertTrue(passwordEncoder.matches(password, account.getPassword()));
    }
}
