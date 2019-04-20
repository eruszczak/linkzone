package pl.reryk.linkzone.utils;

import pl.reryk.linkzone.dto.AccountSummary;
import pl.reryk.linkzone.model.Account;

public class ModelMapper {

    public static AccountSummary mapAccountToSummary(Account account) {
        AccountSummary accountSummary = new AccountSummary();
        accountSummary.setUsername(account.getUsername());
        accountSummary.setJoined(account.getCreatedAt());
        return accountSummary;
    }
}
