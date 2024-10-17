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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PreRegisterUserRepository preRegisterUserRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository, PreRegisterUserRepository preRegisterUserRepository,
                       PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.preRegisterUserRepository = preRegisterUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DataConflictException("Email já cadastrado.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User createdUser = userRepository.save(user);
        createdUser.setPassword(maskPassword(createdUser.getPassword()));
        return createdUser;
    }


    public PreRegisterUser preRegisterUser(PreRegisterUser preRegisterUser) {
        if (preRegisterUserRepository.existsByEmail(preRegisterUser.getEmail())) {
            throw new DataConflictException("Email já cadastrado.");
        }
        preRegisterUser.setPassword(passwordEncoder.encode(preRegisterUser.getPassword()));
        return preRegisterUserRepository.save(preRegisterUser);
    }

    private String maskPassword(String password) {
        return password.substring(0, 4) + "*".repeat(password.length() - 4);
    }

    public User updateUser(Long id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
        if (user.getRole().equals(RoleType.PACIENTE)) {
            throw new DataConflictException("Não é possível atualizar usuários com perfil PACIENTE");
        }
        user.setEmail(updatedUser.getEmail());
        user.setName(updatedUser.getName());
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
        if (user.getRole().equals(RoleType.PACIENTE)) {
            throw new DataConflictException("Não é possível excluir usuários com perfil PACIENTE");
        }
        userRepository.delete(user);
    }

    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(user -> {
            if (!user.getRole().equals(RoleType.PACIENTE)) {
                user.setPassword(maskPassword(user.getPassword()));
            }
            return user;
        });
    }

    public User findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
        if (user.getRole().equals(RoleType.PACIENTE)) {
            throw new UserNotFoundException("Usuário não encontrado");
        }
        user.setPassword(maskPassword(user.getPassword()));
        return user;
    }

    public String loginUser(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                Map<String, Object> claims = Map.of(
                        "id", user.getId(),
                        "email", user.getEmail(),
                        "role", user.getRole().name()
                );
                return jwtUtil.generateToken(claims, email);
            }

            Optional<PreRegisterUser> preRegisterUserOptional = preRegisterUserRepository.findByEmail(email);
            if (preRegisterUserOptional.isPresent()) {
                PreRegisterUser preRegisterUser = preRegisterUserOptional.get();
                Map<String, Object> claims = Map.of(
                        "id", preRegisterUser.getId(),
                        "email", preRegisterUser.getEmail(),
                        "role", preRegisterUser.getRole().name()
                );
                return jwtUtil.generateToken(claims, email);
            }

            throw new UserNotFoundException("Usuário não encontrado");
        } catch (Exception e) {
            throw new UnauthorizedException("Credenciais inválidas");
        }
    }

    public void resetPassword(String email, String newPassword) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email não pode ser vazio");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}