package com.example.reddit.resource;

import com.example.reddit.controller.account.AccountRestController;
import com.example.reddit.model.Account;
import com.example.reddit.utils.ModelMapper;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class AccountResourceAssembler extends ResourceAssemblerSupport<Account, AccountResource> {

    public AccountResourceAssembler() {
        super(AccountRestController.class, AccountResource.class);
    }

    @Override
    public AccountResource toResource(Account account) {
        return new AccountResource(ModelMapper.mapAccountToSummary(account));
    }
}
