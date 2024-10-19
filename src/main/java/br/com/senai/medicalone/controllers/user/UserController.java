package br.com.senai.medicalone.controllers.user;

import br.com.senai.medicalone.dtos.user.ResetPasswordRequestDTO;
import br.com.senai.medicalone.dtos.user.UserRequestDTO;
import br.com.senai.medicalone.entities.user.PreRegisterUser;
import br.com.senai.medicalone.entities.user.RoleType;
import br.com.senai.medicalone.entities.user.User;
import br.com.senai.medicalone.exceptions.customexceptions.BadRequestException;
import br.com.senai.medicalone.exceptions.customexceptions.DataConflictException;
import br.com.senai.medicalone.exceptions.customexceptions.UnauthorizedException;
import br.com.senai.medicalone.exceptions.customexceptions.UserNotFoundException;
import br.com.senai.medicalone.services.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "User Controller", description = "Endpoints para gerenciamento de usuários")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/pre-registro")
    @Operation(summary = "Pre-register a user", description = "Endpoint para pré-registrar um usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário pré-registrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados ausentes ou incorretos"),
            @ApiResponse(responseCode = "409", description = "Email já cadastrado")
    })
    public ResponseEntity<Object> preRegisterUser(@RequestBody PreRegisterUser preRegisterUser) {
        try {
            PreRegisterUser createdUser = userService.preRegisterUser(preRegisterUser);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (DataConflictException e) {
            return new ResponseEntity<>(Map.of("message"

, e.getMessage()), HttpStatus.CONFLICT);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(Map.of("message"

, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    @Operation(summary = "Create a new user", description = "Endpoint para criar um novo usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Role inválida, CPF ausente ou dados ausentes"),
            @ApiResponse(responseCode = "409", description = "Email ou CPF já cadastrado")
    })
    public ResponseEntity<Object> createUser(@Valid @RequestBody User user) {
        if (user.getRole().equals(RoleType.PACIENTE)) {
            return new ResponseEntity<>(Map.of("message"

, "Role inválida ou dados ausentes"), HttpStatus.BAD_REQUEST);
        }
        try {
            User createdUser = userService.createUser(user);
            return new ResponseEntity<>(Map.of("message", "Usuário criado com sucesso", "user", createdUser), HttpStatus.CREATED);
        } catch (DataConflictException e) {
            return new ResponseEntity<>(Map.of("message"

, e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a user", description = "Endpoint para atualizar um usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Role inválida ou dados ausentes"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        if (user.getRole().equals(RoleType.PACIENTE)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        User updatedUser = userService.updateUser(id, user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user", description = "Endpoint para excluir um usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "409", description = "Não é possível excluir usuários com perfil PACIENTE")
    })
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>(Map.of("message", "Usuário excluído com sucesso"), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(Map.of("message"

, e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (DataConflictException e) {
            return new ResponseEntity<>(Map.of("message"

, e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Endpoint para obter um usuário pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.findUserById(id);
        if (user.getRole().equals(RoleType.PACIENTE)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Endpoint para obter todos os usuários")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuários encontrados com sucesso")
    })
    public ResponseEntity<Object> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<User> usersPage = (Page<User>) userService.findAllUsers(PageRequest.of(page, size));
        return new ResponseEntity<>(usersPage, HttpStatus.OK);
    }

    @PostMapping("/login")
    @Operation(summary = "Login a user", description = "Endpoint para login de um usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody UserRequestDTO userRequestDTO) {
        try {
            String jwt = userService.loginUser(userRequestDTO.getEmail(), userRequestDTO.getPassword());
            Map<String, String> response = new HashMap<>();
            response.put("token", jwt);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/email/{email}/redefinir-senha")
    @Operation(summary = "Reset password", description = "Endpoint para redefinir a senha de um usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Senha redefinida com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados ausentes ou incorretos")
    })
    public ResponseEntity<Map<String, String>> resetPassword(@PathVariable String email, @RequestBody ResetPasswordRequestDTO request) {
        if (request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
            throw new BadRequestException("Dados ausentes ou incorretos");
        }
        userService.resetPassword(email, request.getNewPassword());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Senha redefinida com sucesso");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}