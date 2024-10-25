package br.com.senai.medicalone.services.user;

import br.com.senai.medicalone.dtos.user.UserRequestDTO;
import br.com.senai.medicalone.dtos.user.UserResponseDTO;
import br.com.senai.medicalone.entities.user.PreRegisterUser;
import br.com.senai.medicalone.entities.user.RoleType;
import br.com.senai.medicalone.entities.user.User;
import br.com.senai.medicalone.exceptions.customexceptions.*;
import br.com.senai.medicalone.repositories.user.PreRegisterUserRepository;
import br.com.senai.medicalone.repositories.user.UserRepository;
import br.com.senai.medicalone.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Operation(summary = "Criar um novo usuário", description = "Método para criar um novo usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "409", description = "Email ou CPF já cadastrado")
    })
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        validateUserFields(userRequestDTO);
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new DataConflictException("Email já cadastrado.");
        }
        if (userRepository.existsByCpf(userRequestDTO.getCpf())) {
            throw new DataConflictException("CPF já cadastrado.");
        }
        User user = new User();
        user.setName(userRequestDTO.getName());
        user.setEmail(userRequestDTO.getEmail());
        user.setBirthDate(userRequestDTO.getBirthDate());
        user.setPhone(userRequestDTO.getPhone());
        user.setCpf(userRequestDTO.getCpf());
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        user.setRole(userRequestDTO.getRole());
        User savedUser = userRepository.save(user);
        return convertToUserResponseDTO(savedUser);
    }

    @Operation(summary = "Pré-registrar um usuário", description = "Método para pré-registrar um usuário")
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

    private void validateUserFields(UserRequestDTO userRequestDTO) {
        if (userRequestDTO.getName() == null || userRequestDTO.getName().isEmpty() ||
                userRequestDTO.getEmail() == null || userRequestDTO.getEmail().isEmpty() ||
                userRequestDTO.getBirthDate() == null ||
                userRequestDTO.getPhone() == null || userRequestDTO.getPhone().isEmpty() ||
                userRequestDTO.getCpf() == null || userRequestDTO.getCpf().isEmpty() ||
                userRequestDTO.getPassword() == null || userRequestDTO.getPassword().isEmpty() ||
                userRequestDTO.getRole() == null) {
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

    @Operation(summary = "Atualizar um usuário", description = "Método para atualizar um usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "409", description = "Não é possível atualizar usuários com perfil PACIENTE")
    })
    public UserResponseDTO updateUser(Long id, UserRequestDTO updatedUserDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
        if (user.getRole().equals(RoleType.PACIENTE)) {
            throw new DataConflictException("Não é possível atualizar usuários com perfil PACIENTE");
        }
        user.setEmail(updatedUserDTO.getEmail());
        user.setName(updatedUserDTO.getName());
        User updatedUser = userRepository.save(user);
        return convertToUserResponseDTO(updatedUser);
    }

    @Operation(summary = "Excluir um usuário", description = "Método para excluir um usuário")
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

    @Operation(summary = "Obter todos os usuários", description = "Método para obter todos os usuários")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuários encontrados com sucesso")
    })
    public Page<UserResponseDTO> findAllUsers(Pageable pageable) {
        Page<User> usersPage = userRepository.findAll(pageable);
        List<UserResponseDTO> filteredUsers = usersPage.stream()
                .filter(user -> !user.getRole().equals(RoleType.PACIENTE))
                .map(this::convertToUserResponseDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(filteredUsers, pageable, usersPage.getTotalElements());
    }

    @Operation(summary = "Obter usuário pelo ID", description = "Método para obter um usuário pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public UserResponseDTO findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
        if (user.getRole().equals(RoleType.PACIENTE)) {
            throw new UserNotFoundException("Usuário não encontrado");
        }
        user.setPassword(maskPassword(user.getPassword()));
        return convertToUserResponseDTO(user);
    }

    @Operation(summary = "Login de um usuário", description = "Método para login de um usuário")
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
                return jwtUtil.generateToken(user);
            }

            Optional<PreRegisterUser> preRegisterUserOptional = preRegisterUserRepository.findByEmail(email);
            if (preRegisterUserOptional.isPresent()) {
                PreRegisterUser preRegisterUser = preRegisterUserOptional.get();
                return jwtUtil.generateToken(preRegisterUser);
            }

            throw new UserNotFoundException("Usuário não encontrado");
        } catch (Exception e) {
            throw new UnauthorizedException("Credenciais inválidas");
        }
    }

    @Operation(summary = "Redefinir senha", description = "Método para redefinir a senha de um usuário")
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

    public UserResponseDTO convertToUserResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setBirthDate(user.getBirthDate());
        dto.setPhone(user.getPhone());
        dto.setCpf(user.getCpf());
        dto.setRole(user.getRole());
        return dto;
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByEmail(username);
    }
}