package br.com.senai.medicalone.services.auth;

import br.com.senai.medicalone.entities.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public static Long getAuthenticatedPatientId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            User user = (User) principal;
            if (user.getPatientId() == null) {
                throw new RuntimeException("Paciente não associado ao usuário autenticado");
            }
            return user.getPatientId();
        } else {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }
    }
}