package com.example.reddit.utils;

import com.example.reddit.dto.AccountSummary;
import com.example.reddit.model.Account;

public class ModelMapper {

    public static AccountSummary mapAccountToSummary(Account account) {
        AccountSummary accountSummary = new AccountSummary();
        accountSummary.setUsername(account.getUsername());
        accountSummary.setJoined(account.getCreatedAt());
        return accountSummary;
    }
}
