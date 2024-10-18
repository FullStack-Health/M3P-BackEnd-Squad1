package br.com.senai.medicalone.services.user;

import br.com.senai.medicalone.entities.user.PreRegisterUser;
import br.com.senai.medicalone.entities.user.User;
import br.com.senai.medicalone.repositories.user.PreRegisterUserRepository;
import br.com.senai.medicalone.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final PreRegisterUserRepository preRegisterUserRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository, PreRegisterUserRepository preRegisterUserRepository) {
        this.userRepository = userRepository;
        this.preRegisterUserRepository = preRegisterUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElse(null);

        if (user != null) {
            return user;
        }

        PreRegisterUser preRegisterUser = preRegisterUserRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + username));

        return preRegisterUser;
    }
}