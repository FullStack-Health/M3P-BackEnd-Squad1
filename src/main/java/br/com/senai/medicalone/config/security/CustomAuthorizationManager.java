package br.com.senai.medicalone.config.security;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class CustomAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final UserSecurity userSecurity;

    public CustomAuthorizationManager(UserSecurity userSecurity) {
        this.userSecurity = userSecurity;
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        Authentication auth = authentication.get();
        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            String path = context.getRequest().getRequestURI();
            Long id = Long.valueOf(path.substring(path.lastIndexOf('/') + 1));

            boolean isSelf = userSecurity.isSelf(auth, id);
            return new AuthorizationDecision(isSelf || auth.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN")));
        }
        return new AuthorizationDecision(false);
    }
}