package pl.reryk.linkzone.security;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SpringSecurityUserContext implements UserContext {

    @Override
    public String context() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getPrincipal().toString();
    }
}
