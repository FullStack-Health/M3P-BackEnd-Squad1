package br.com.senai.medicalone.config.security;

import br.com.senai.medicalone.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class UserSecurity {

    private final UserService userService;

    @Autowired
    public UserSecurity(@Lazy UserService userService) {
        this.userService = userService;
    }

    public boolean isSelf(Authentication authentication, Long id) {
        String username = authentication.getName();
        return userService.findById(id)
                .map(user -> user.getEmail().equals(username))
                .orElse(false);
    }

    public boolean isSelf(Authentication authentication) {
        String username = authentication.getName();
        return userService.findByUsername(username)
                .map(user -> user.getEmail().equals(username))
                .orElse(false);
    }
}