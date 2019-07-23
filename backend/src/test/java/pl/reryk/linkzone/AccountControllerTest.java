package pl.reryk.linkzone;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.reryk.linkzone.controller.account.AccountRestController;
import pl.reryk.linkzone.model.Account;
import pl.reryk.linkzone.service.AccountService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Ignore
@RunWith(SpringRunner.class)
@WebMvcTest(AccountRestController.class)
public class AccountControllerTest {

    @MockBean
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void accountNotFound() throws Exception {
        Account account = new Account();
        account.setUsername("daa");
        when(accountService.findByUsername(any(String.class))).thenReturn(account);

        mockMvc.perform(get("/api/users/notExistingUsername")
                .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(account.getUsername()));
    }

}
