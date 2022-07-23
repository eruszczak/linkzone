package io.eryk.linkzone.utils;

import io.eryk.linkzone.dto.AccountSummary;
import io.eryk.linkzone.model.Account;

public class ModelMapper {

    public static AccountSummary mapAccountToSummary(Account account) {
        AccountSummary accountSummary = new AccountSummary();
        accountSummary.setUsername(account.getUsername());
        accountSummary.setJoined(account.getCreatedAt());
        return accountSummary;
    }
}
