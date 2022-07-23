package io.eryk.linkzone.service;

import io.eryk.linkzone.dto.AccountCreate;
import io.eryk.linkzone.dto.AccountUpdate;
import io.eryk.linkzone.dto.IAccountStatsDto;
import io.eryk.linkzone.exception.UserNotFoundException;
import io.eryk.linkzone.model.Account;
import io.eryk.linkzone.model.Group;
import io.eryk.linkzone.model.Role;
import io.eryk.linkzone.permissions.RoleName;
import io.eryk.linkzone.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleService roleService;

    private final GroupService groupService;

    private final GroupMembershipService groupMembershipService;

    @Autowired
    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder, RoleService roleService,
                          GroupService groupService, GroupMembershipService groupMembershipService) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.groupService = groupService;
        this.groupMembershipService = groupMembershipService;
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Page<Account> findAll(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    public void updateAvatar(String avatar, Long userId) {
        accountRepository.updateAvatar(avatar, userId);
    }

    public Account create(AccountCreate accountCreate) {
        Account account = prepare(accountCreate, RoleName.USER);
        account = save(account);
        List<Group> defaultGroups = groupService.findDefaultGroups();
        for (Group group : defaultGroups) {
            groupMembershipService.subscribe(group, account);
        }
        return account;
    }

    public Account create(AccountCreate accountCreate, RoleName roleName) {
        Account account = prepare(accountCreate, roleName);
        return save(account);
    }

    private Account prepare(AccountCreate accountCreate, RoleName roleName) {
        Account account = new Account();
        account.setEmail(accountCreate.getEmail());
        account.setUsername(accountCreate.getUsername());
        account.setPassword(encodePassword(accountCreate.getPassword()));
        Role userRole = roleService.findByName(roleName);
        account.setRoles(Collections.singleton(userRole));
        return account;
    }

    public Account save(Account account) {
        account.setEmail(account.getEmail().toLowerCase());
        return accountRepository.save(account);
    }

    public Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));
    }

    public boolean existsById(Long aLong) {
        return accountRepository.existsById(aLong);
    }

    public long count() {
        return accountRepository.count();
    }

    public void deleteById(Long aLong) {
        accountRepository.deleteById(aLong);
    }

    public void delete(Account account) {
        accountRepository.delete(account);
    }

    public void update(Account account, AccountUpdate accountUpdate) {
        account.setUsername(accountUpdate.getUsername());
        account.setEmail(accountUpdate.getEmail());
        account.setTagline(accountUpdate.getTagline());
        if (accountUpdate.getPassword() != null) {
            account.setPassword(encodePassword(accountUpdate.getPassword()));
        }
        save(account);
    }

    public IAccountStatsDto calculateStats(Long userId) {
        return accountRepository.calculateStats(userId);
    }

    public Account findByUsername(String username) {
        return accountRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    @Transactional
    public Account findByUsernameEager(String username) {
        Account account = accountRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        account.getModeratedGroups().size();
        account.getAdministratedGroups().size();
        return account;
    }

    public Account findByEmail(String email) {
        return accountRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    public void deleteAll() {
        accountRepository.deleteAll();
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public Account findByUsernameOrEmail(String username, String email) {
        return accountRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase(username, email)
                .orElseThrow(() -> new UserNotFoundException(username));
    }
}
