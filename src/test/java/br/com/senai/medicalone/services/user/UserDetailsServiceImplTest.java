package br.com.senai.medicalone.services.user;
import br.com.senai.medicalone.entities.user.PreRegisterUser;
import br.com.senai.medicalone.entities.user.User;
import br.com.senai.medicalone.repositories.user.PreRegisterUserRepository;
import br.com.senai.medicalone.repositories.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PreRegisterUserRepository preRegisterUserRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User user;
    private PreRegisterUser preRegisterUser;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setEmail("user@example.com");

        preRegisterUser = new PreRegisterUser();
        preRegisterUser.setEmail("preuser@example.com");
    }

    @Test
    public void testLoadUserByUsername_UserExists() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("user@example.com");

        assertNotNull(userDetails);
        assertEquals("user@example.com", userDetails.getUsername());
    }

    @Test
    public void testLoadUserByUsername_PreRegisterUserExists() {
        when(userRepository.findByEmail("preuser@example.com")).thenReturn(Optional.empty());
        when(preRegisterUserRepository.findByEmail("preuser@example.com")).thenReturn(Optional.of(preRegisterUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("preuser@example.com");

        assertNotNull(userDetails);
        assertEquals("preuser@example.com", userDetails.getUsername());
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());
        when(preRegisterUserRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("notfound@example.com");
        });
    }
}