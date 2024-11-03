package br.com.senai.medicalone.controllers.user;

import br.com.senai.medicalone.dtos.login.LoginRequestDTO;
import br.com.senai.medicalone.dtos.user.ResetPasswordRequestDTO;
import br.com.senai.medicalone.dtos.user.UserRequestDTO;
import br.com.senai.medicalone.dtos.user.UserResponseDTO;
import br.com.senai.medicalone.dtos.user.UserUpdateRequestDTO;
import br.com.senai.medicalone.entities.user.PreRegisterUser;
import br.com.senai.medicalone.entities.user.RoleType;
import br.com.senai.medicalone.exceptions.customexceptions.BadRequestException;
import br.com.senai.medicalone.exceptions.customexceptions.DataConflictException;
import br.com.senai.medicalone.exceptions.customexceptions.UnauthorizedException;
import br.com.senai.medicalone.exceptions.customexceptions.UserNotFoundException;
import br.com.senai.medicalone.services.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "User Controller", description = "Endpoints para gerenciamento de usuários")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PagedResourcesAssembler<UserResponseDTO> pagedResourcesAssembler;

    @PostMapping("/pre-registro")
    @Operation(summary = "Pre registro de um usuario", description = "Endpoint para pré-registrar um usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário pré-registrado com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Usuário pré-cadastrado com sucesso\"}"))),
            @ApiResponse(responseCode = "400", description = "Dados ausentes ou incorretos", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Dados ausentes ou incorretos\"}"))),
            @ApiResponse(responseCode = "409", description = "Email já cadastrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Email já cadastrado\"}")))
    })
    public ResponseEntity<Map<String, String>> preRegisterUser(@RequestBody PreRegisterUser preRegisterUser) {
        try {
            userService.preRegisterUser(preRegisterUser);
            return new ResponseEntity<>(Map.of("message", "Usuário pré-cadastrado com sucesso"), HttpStatus.CREATED);
        } catch (DataConflictException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.CONFLICT);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    @Operation(summary = "Cria um novo usuario", description = "Endpoint para criar um novo usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Usuário criado com sucesso\", \"user\": {\"id\": 1, \"name\": \"John Doe\", \"email\": \"user94@example.com\", \"birthDate\": \"1990-01-01\", \"phone\": \"1234567890\", \"cpf\": \"12345678148\", \"role\": \"ADMIN\"}}"))),
            @ApiResponse(responseCode = "400", description = "Role inválida, CPF ausente ou dados ausentes", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Role inválida ou dados ausentes\"}"))),
            @ApiResponse(responseCode = "409", description = "Email ou CPF já cadastrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Email ou CPF já cadastrado\"}")))
    })
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        if (userRequestDTO.getRole().equals(RoleType.PACIENTE)) {
            return new ResponseEntity<>(Map.of("message", "Role inválida ou dados ausentes"), HttpStatus.BAD_REQUEST);
        }
        try {
            UserResponseDTO createdUser = userService.createUser(userRequestDTO);
            return new ResponseEntity<>(Map.of("message", "Usuário criado com sucesso", "user", createdUser), HttpStatus.CREATED);
        } catch (DataConflictException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um usuario", description = "Endpoint para atualizar um usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Usuário atualizado com sucesso\"}"))),
            @ApiResponse(responseCode = "400", description = "Role inválida ou dados ausentes", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Role inválida ou dados ausentes\"}"))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Usuário não encontrado\"}")))
    })
    public ResponseEntity<Map<String, String>> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequestDTO userUpdateRequestDTO) {
        try {
            userService.updateUser(id, userUpdateRequestDTO);
            return new ResponseEntity<>(Map.of("message", "Usuário atualizado com sucesso"), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(Map.of("message", "Usuário não encontrado"), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta um usuario", description = "Endpoint para excluir um usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário excluído com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Usuário excluído com sucesso\"}"))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Usuário não encontrado\"}"))),
            @ApiResponse(responseCode = "409", description = "Não é possível excluir usuários com perfil PACIENTE", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Não é possível excluir usuários com perfil PACIENTE\"}")))
    })
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>(Map.of("message", "Usuário excluído com sucesso"), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (DataConflictException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca usuario por ID", description = "Endpoint para obter um usuário pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Usuário encontrado com sucesso\", \"user\": {\"id\": 1, \"name\": \"John Doe\", \"email\": \"user@example.com\", \"birthDate\": \"1990-01-01\", \"phone\": \"(99) 9 9999-9999\", \"cpf\": \"123.456.789-00\", \"role\": \"ADMIN\"}}"))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Usuário não encontrado\"}")))
    })
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        try {
            UserResponseDTO user = userService.findUserById(id);
            return new ResponseEntity<>(Map.of("message", "Usuário encontrado com sucesso", "user", user), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    @Operation(summary = "Busca todos os usuarios", description = "Endpoint para obter todos os usuários")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuários encontrados com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Usuários encontrados com sucesso\", \"users\": [{\"id\": 1, \"name\": \"John Doe\", \"email\": \"user@example.com\", \"birthDate\": \"1990-01-01\", \"phone\": \"(99) 9 9999-9999\", \"cpf\": \"123.456.789-00\", \"role\": \"ADMIN\"}]}")))
    })
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email) {

        Page<UserResponseDTO> usersPage = userService.findAllUsers(PageRequest.of(page, size), id, name, email);
        PagedModel<EntityModel<UserResponseDTO>> pagedModel = pagedResourcesAssembler.toModel(usersPage);
        List<UserResponseDTO> users = pagedModel.getContent().stream()
                .map(EntityModel::getContent)
                .collect(Collectors.toList());

        Map<String, Object> response = Map.of(
                "message", "Usuários encontrados com sucesso",
                "users", users,
                "page", Map.of(
                        "size", usersPage.getSize(),
                        "totalElements", usersPage.getTotalElements(),
                        "totalPages", usersPage.getTotalPages(),
                        "number", usersPage.getNumber()
                )
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/login")
    @Operation(summary = "Login de usuario", description = "Endpoint para login de um usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"token\": \"jwt-token\"}"))),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Credenciais inválidas\"}")))
    })
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            String jwt = userService.loginUser(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());
            Map<String, String> response = new HashMap<>();
            response.put("token", jwt);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/email/{email}/redefinir-senha")
    @Operation(summary = "Reseta a senha", description = "Endpoint para redefinir a senha de um usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Senha redefinida com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Senha redefinida com sucesso\"}"))),
            @ApiResponse(responseCode = "400", description = "Dados ausentes ou incorretos", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Dados ausentes ou incorretos\"}")))
    })
    public ResponseEntity<Map<String, String>> resetPassword(@PathVariable String email, @RequestBody ResetPasswordRequestDTO request) {
        if (request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
            return new ResponseEntity<>(Map.of("message", "Dados ausentes ou incorretos"), HttpStatus.BAD_REQUEST);
        }
        userService.resetPassword(email, request.getNewPassword());
        return new ResponseEntity<>(Map.of("message", "Senha redefinida com sucesso"), HttpStatus.OK);
    }
}