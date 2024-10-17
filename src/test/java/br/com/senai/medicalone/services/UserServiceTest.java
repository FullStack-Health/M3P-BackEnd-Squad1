package br.com.senai.medicalone.services;

import br.com.senai.medicalone.entities.PreRegisterUser;
import br.com.senai.medicalone.entities.RoleType;
import br.com.senai.medicalone.entities.User;
import br.com.senai.medicalone.exceptions.customexceptions.DataConflictException;
import br.com.senai.medicalone.exceptions.customexceptions.UnauthorizedException;
import br.com.senai.medicalone.exceptions.customexceptions.UserNotFoundException;
import br.com.senai.medicalone.repositories.PreRegisterUserRepository;
import br.com.senai.medicalone.repositories.UserRepository;
import br.com.senai.medicalone.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PreRegisterUserRepository preRegisterUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    @Test
    public void testPreRegisterUser_Success() {
        PreRegisterUser preRegisterUser = new PreRegisterUser();
        preRegisterUser.setEmail("test@example.com");
        preRegisterUser.setPassword("password");
        preRegisterUser.setRole(RoleType.ADMIN);

        when(preRegisterUserRepository.existsByEmail(preRegisterUser.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(preRegisterUser.getPassword())).thenReturn("encodedPassword");
        when(preRegisterUserRepository.save(preRegisterUser)).thenReturn(preRegisterUser);

        PreRegisterUser createdPreRegisterUser = userService.preRegisterUser(preRegisterUser);

        assertNotNull(createdPreRegisterUser);
        assertEquals("test@example.com", createdPreRegisterUser.getEmail());
        verify(preRegisterUserRepository, times(1)).save(preRegisterUser);
    }

    @Test
    public void testPreRegisterUser_EmailAlreadyExists() {
        PreRegisterUser preRegisterUser = new PreRegisterUser();
        preRegisterUser.setEmail("test@example.com");

        when(preRegisterUserRepository.existsByEmail(preRegisterUser.getEmail())).thenReturn(true);

        assertThrows(DataConflictException.class, () -> userService.preRegisterUser(preRegisterUser));
    }

    @Test
    public void testUpdateUser_Success() {
        User user = new User();
        user.setId(1L);
        user.setRole(RoleType.ADMIN);

        User updatedUser = new User();
        updatedUser.setEmail("updated@example.com");
        updatedUser.setName("Updated Name");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.updateUser(1L, updatedUser);

        assertNotNull(result);
        assertEquals("updated@example.com", result.getEmail());
        assertEquals("Updated Name", result.getName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testUpdateUser_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        User updatedUser = new User();
        updatedUser.setEmail("updated@example.com");

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(1L, updatedUser));
    }

    @Test
    public void testDeleteUser_Success() {
        User user = new User();
        user.setId(1L);
        user.setRole(RoleType.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void testDeleteUser_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    public void testFindAllUsers_Success() {
        User user = new User();
        user.setRole(RoleType.ADMIN);
        Page<User> page = new PageImpl<>(Collections.singletonList(user));

        when(userRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<User> result = userService.findAllUsers(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    public void testLoginUser_Success() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(RoleType.ADMIN);

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(anyMap(), eq("test@example.com"))).thenReturn("token");

        String token = userService.loginUser("test@example.com", "password");

        assertNotNull(token);
        assertEquals("token", token);
    }

    @Test
    public void testLoginUser_InvalidCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new UnauthorizedException("Credenciais inválidas"));

        assertThrows(UnauthorizedException.class, () -> userService.loginUser("test@example.com", "wrongpassword"));
    }

    @Test
    public void testResetPassword_Success() {
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");

        userService.resetPassword("test@example.com", "newpassword");

        verify(userRepository, times(1)).save(user);
        assertEquals("encodedNewPassword", user.getPassword());
    }

    @Test
    public void testResetPassword_UserNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.resetPassword("test@example.com", "newpassword"));
    }
}