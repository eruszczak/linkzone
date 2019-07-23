package pl.reryk.linkzone;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.reryk.linkzone.controller.account.AccountRestController;
import pl.reryk.linkzone.service.AccountService;

@Ignore
//@WebIntegrationTest
public class ExampleTest {

    @InjectMocks
    AccountRestController accountRestController;

    @Mock
    AccountService accountService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void name() {

    }

    @Test
    public void test() {
        accountRestController.checkEmailAvailability("dsad");
    }
}
