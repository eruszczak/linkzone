package pl.reryk.linkzone.resource;

import pl.reryk.linkzone.controller.account.AccountRestController;
import pl.reryk.linkzone.model.Account;
import pl.reryk.linkzone.utils.ModelMapper;
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
