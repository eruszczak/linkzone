package pl.reryk.linkzone.security;

import pl.reryk.linkzone.model.Account;
import pl.reryk.linkzone.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountService accountService;

    @Autowired
    public CustomUserDetailsService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        Account account = accountService.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        return UserPrincipal.create(account);
    }

    // This method is used by JWTAuthenticationFilter because token contains user id
    public UserDetails loadUserById(Long id) {
        Account account = accountService.findById(id);
        return UserPrincipal.create(account);
    }
}