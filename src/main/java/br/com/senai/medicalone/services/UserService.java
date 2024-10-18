package br.com.senai.medicalone.services;

import br.com.senai.medicalone.entities.PreRegisterUser;
import br.com.senai.medicalone.entities.RoleType;
import br.com.senai.medicalone.entities.User;
import br.com.senai.medicalone.exceptions.customexceptions.*;
import br.com.senai.medicalone.repositories.PreRegisterUserRepository;
import br.com.senai.medicalone.repositories.UserRepository;
import br.com.senai.medicalone.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Create a new user", description = "Método para criar um novo usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "409", description = "Email ou CPF já cadastrado")
    })
    public User createUser(User user) {
        validateUserFields(user);
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DataConflictException("Email já cadastrado.");
        }
        if (userRepository.existsByCpf(user.getCpf())) {
            throw new DataConflictException("CPF já cadastrado.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User createdUser = userRepository.save(user);
        createdUser.setPassword(maskPassword(createdUser.getPassword()));
        return createdUser;
    }

    @Operation(summary = "Pre-register a user", description = "Método para pré-registrar um usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário pré-registrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados ausentes ou incorretos"),
            @ApiResponse(responseCode = "409", description = "Email já cadastrado")
    })
    public PreRegisterUser preRegisterUser(PreRegisterUser preRegisterUser) {
        if (preRegisterUserRepository.existsByEmail(preRegisterUser.getEmail())) {
            throw new DataConflictException("Email já cadastrado.");
        }
        validatePreRegisterUserFields(preRegisterUser);
        if (preRegisterUser.getRole() != RoleType.ADMIN && preRegisterUser.getRole() != RoleType.MEDICO) {
            throw new BadRequestException("Role inválida. Somente ADMIN ou MEDICO são permitidos.");
        }
        preRegisterUser.setPassword(passwordEncoder.encode(preRegisterUser.getPassword()));
        return preRegisterUserRepository.save(preRegisterUser);
    }

    private void validateUserFields(User user) {
        if (user.getName() == null || user.getName().isEmpty() ||
                user.getEmail() == null || user.getEmail().isEmpty() ||
                user.getBirthDate() == null ||
                user.getPhone() == null || user.getPhone().isEmpty() ||
                user.getCpf() == null || user.getCpf().isEmpty() ||
                user.getPassword() == null || user.getPassword().isEmpty() ||
                user.getRole() == null) {
            throw new ValidationException("Dados ausentes ou incorretos");
        }
    }

    private void validatePreRegisterUserFields(PreRegisterUser preRegisterUser) {
        if (preRegisterUser.getEmail() == null || preRegisterUser.getEmail().isEmpty() ||
                preRegisterUser.getPassword() == null || preRegisterUser.getPassword().isEmpty() ||
                preRegisterUser.getRole() == null) {
            throw new BadRequestException("Dados ausentes ou incorretos");
        }
    }


    private String maskPassword(String password) {
        if (password == null) {
            return null;
        }
        return password.substring(0, 4) + "*".repeat(password.length() - 4);
    }

    @Operation(summary = "Update a user", description = "Método para atualizar um usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "409", description = "Não é possível atualizar usuários com perfil PACIENTE")
    })
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

    @Operation(summary = "Delete a user", description = "Método para excluir um usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "409", description = "Não é possível excluir usuários com perfil PACIENTE")
    })
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
        if (user.getRole().equals(RoleType.PACIENTE)) {
            throw new DataConflictException("Não é possível excluir usuários com perfil PACIENTE");
        }
        userRepository.delete(user);
    }

    @Operation(summary = "Get all users", description = "Método para obter todos os usuários")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuários encontrados com sucesso")
    })
    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(user -> {
            if (!user.getRole().equals(RoleType.PACIENTE)) {
                user.setPassword(maskPassword(user.getPassword()));
            }
            return user;
        });
    }

    @Operation(summary = "Get user by ID", description = "Método para obter um usuário pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public User findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
        if (user.getRole().equals(RoleType.PACIENTE)) {
            throw new UserNotFoundException("Usuário não encontrado");
        }
        user.setPassword(maskPassword(user.getPassword()));
        return user;
    }

    @Operation(summary = "Login a user", description = "Método para login de um usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
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

    @Operation(summary = "Reset password", description = "Método para redefinir a senha de um usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Senha redefinida com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados ausentes ou incorretos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
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