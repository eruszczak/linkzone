package io.eryk.linkzone.dto;

import io.eryk.linkzone.model.Account;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class AccountGroupInfo {

    List<GroupResponseShort> moderatedGroups;
    List<GroupResponseShort> administratedGroups;

    public AccountGroupInfo() {
    }

    public AccountGroupInfo(Account account) {
        moderatedGroups = account.getModeratedGroups().stream().map(GroupResponseShort::new).collect(Collectors.toList());
        administratedGroups = account.getAdministratedGroups().stream().map(GroupResponseShort::new).collect(Collectors.toList());
    }
}
