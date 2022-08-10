package io.eryk.linkzone.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
//@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Slf4j
public class SpringSecurityUserContext implements UserContext {

    @Override
    public Long context() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long result = null;
        if (!authentication.getPrincipal().equals("anonymousUser")) {
            result = ((UserPrincipal) authentication.getPrincipal()).getId();
        }
        log.debug("UserContext is: {}", result);
        return result;
    }
}
