package br.com.senai.medicalone.services.user;

import br.com.senai.medicalone.dtos.user.UserRequestDTO;
import br.com.senai.medicalone.dtos.user.UserResponseDTO;
import br.com.senai.medicalone.dtos.user.UserUpdateRequestDTO;
import br.com.senai.medicalone.entities.user.PreRegisterUser;
import br.com.senai.medicalone.entities.user.RoleType;
import br.com.senai.medicalone.entities.user.User;
import br.com.senai.medicalone.exceptions.customexceptions.*;
import br.com.senai.medicalone.repositories.user.PreRegisterUserRepository;
import br.com.senai.medicalone.repositories.user.UserRepository;
import br.com.senai.medicalone.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
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

    private User user;
    private PreRegisterUser preRegisterUser;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(RoleType.ADMIN);

        preRegisterUser = new PreRegisterUser();
        preRegisterUser.setId(2L);
        preRegisterUser.setEmail("pretest@example.com");
        preRegisterUser.setPassword(passwordEncoder.encode("password"));
        preRegisterUser.setRole(RoleType.ADMIN);
    }

    @Test
    public void testCreateUser_Success() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setName("Test User");
        userRequestDTO.setEmail("test@example.com");
        userRequestDTO.setBirthDate(LocalDate.now());
        userRequestDTO.setPhone("123456789");
        userRequestDTO.setCpf("12345678900");
        userRequestDTO.setPassword("password");
        userRequestDTO.setRole(RoleType.ADMIN);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Test User");
        savedUser.setEmail("test@example.com");
        savedUser.setBirthDate(LocalDate.now());
        savedUser.setPhone("123456789");
        savedUser.setCpf("12345678900");
        savedUser.setPassword("encodedPassword");
        savedUser.setRole(RoleType.ADMIN);

        when(userRepository.existsByEmail(userRequestDTO.getEmail())).thenReturn(false);
        when(userRepository.existsByCpf(userRequestDTO.getCpf())).thenReturn(false);
        when(passwordEncoder.encode(userRequestDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponseDTO createdUser = userService.createUser(userRequestDTO);

        assertNotNull(createdUser);
        assertEquals("test@example.com", createdUser.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testCreateUser_EmailAlreadyExists() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setName("Test User");
        userRequestDTO.setEmail("test@example.com");
        userRequestDTO.setBirthDate(LocalDate.now());
        userRequestDTO.setPhone("123456789");
        userRequestDTO.setCpf("12345678900");
        userRequestDTO.setPassword("password");
        userRequestDTO.setRole(RoleType.ADMIN);

        when(userRepository.existsByEmail(userRequestDTO.getEmail())).thenReturn(true);

        assertThrows(DataConflictException.class, () -> userService.createUser(userRequestDTO));
    }

    @Test
    public void testCreateUser_CpfAlreadyExists() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setName("Test User");
        userRequestDTO.setEmail("test@example.com");
        userRequestDTO.setBirthDate(LocalDate.now());
        userRequestDTO.setPhone("123456789");
        userRequestDTO.setCpf("12345678900");
        userRequestDTO.setPassword("password");
        userRequestDTO.setRole(RoleType.ADMIN);

        when(userRepository.existsByCpf(userRequestDTO.getCpf())).thenReturn(true);

        assertThrows(DataConflictException.class, () -> userService.createUser(userRequestDTO));
    }

    @Test
    public void testUpdateUser_Success() {
        UserUpdateRequestDTO updatedUserDTO = new UserUpdateRequestDTO();
        updatedUserDTO.setEmail("updated@example.com");
        updatedUserDTO.setName("Updated Name");
        updatedUserDTO.setBirthDate(LocalDate.now());
        updatedUserDTO.setPhone("987654321");
        updatedUserDTO.setCpf("98765432100");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO result = userService.updateUser(1L, updatedUserDTO);

        assertNotNull(result);
        assertEquals("updated@example.com", result.getEmail());
        assertEquals("Updated Name", result.getName());
        assertEquals("987654321", result.getPhone());
        assertEquals("98765432100", result.getCpf());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testUpdateUser_UserNotFound() {
        UserUpdateRequestDTO updatedUserDTO = new UserUpdateRequestDTO();
        updatedUserDTO.setEmail("updated@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(1L, updatedUserDTO));
    }

    @Test
    public void testDeleteUser_Success() {
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

        Page<UserResponseDTO> result = userService.findAllUsers(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    public void testLoginUser_Success() {
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(user)).thenReturn("token");

        String token = userService.loginUser("test@example.com", "password");
        assertNotNull(token);
        assertEquals("token", token);
    }

    @Test
    public void testLoginUser_PreRegisterUser_Success() {
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByEmail("pretest@example.com")).thenReturn(Optional.empty());
        when(preRegisterUserRepository.findByEmail("pretest@example.com")).thenReturn(Optional.of(preRegisterUser));
        when(jwtUtil.generateToken(preRegisterUser)).thenReturn("token");

        String token = userService.loginUser("pretest@example.com", "password");
        assertNotNull(token);
        assertEquals("token", token);
    }

    @Test
    public void testLoginUser_UserNotFound() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new UsernameNotFoundException("Usuário não encontrado"));
        assertThrows(UnauthorizedException.class, () -> {
            userService.loginUser("test@example.com", "wrongpassword");
        });
    }

    @Test
    public void testLoginUser_InvalidCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new UnauthorizedException("Credenciais inválidas"));

        assertThrows(UnauthorizedException.class, () -> userService.loginUser("test@example.com", "wrongpassword"));
    }

    @Test
    public void testResetPassword_Success() {
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

    @Test
    public void testCreateUser_MissingFields() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();

        assertThrows(ValidationException.class, () -> userService.createUser(userRequestDTO));
    }

    @Test
    public void testPreRegisterUser_Success() {
        PreRegisterUser preRegisterUser = new PreRegisterUser();
        preRegisterUser.setEmail("test@example.com");
        preRegisterUser.setPassword("password");
        preRegisterUser.setRole(RoleType.ADMIN);

        when(preRegisterUserRepository.existsByEmail(preRegisterUser.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(preRegisterUser.getPassword())).thenReturn("encodedPassword");
        when(preRegisterUserRepository.save(any(PreRegisterUser.class))).thenReturn(preRegisterUser);

        PreRegisterUser result = userService.preRegisterUser(preRegisterUser);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(preRegisterUserRepository, times(1)).save(any(PreRegisterUser.class));
    }

    @Test
    public void testPreRegisterUser_EmailAlreadyExists() {
        PreRegisterUser preRegisterUser = new PreRegisterUser();
        preRegisterUser.setEmail("test@example.com");

        when(preRegisterUserRepository.existsByEmail(preRegisterUser.getEmail())).thenReturn(true);

        assertThrows(DataConflictException.class, () -> userService.preRegisterUser(preRegisterUser));
    }

    @Test
    public void testPreRegisterUser_InvalidRole() {
        PreRegisterUser preRegisterUser = new PreRegisterUser();
        preRegisterUser.setEmail("test@example.com");
        preRegisterUser.setPassword("password");
        preRegisterUser.setRole(RoleType.PACIENTE);

        assertThrows(BadRequestException.class, () -> userService.preRegisterUser(preRegisterUser));
    }

    @Test
    public void testFindUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDTO result = userService.findUserById(1L);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    public void testFindUserById_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findUserById(1L));
    }
}