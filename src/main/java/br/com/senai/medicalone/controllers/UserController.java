package br.com.senai.medicalone.controllers;

import br.com.senai.medicalone.dtos.users.UserRequestDTO;
import br.com.senai.medicalone.entities.RoleType;
import br.com.senai.medicalone.entities.User;
import br.com.senai.medicalone.exceptions.customexceptions.DataConflictException;
import br.com.senai.medicalone.exceptions.customexceptions.UnauthorizedException;
import br.com.senai.medicalone.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/pre-registro")
    public ResponseEntity<User> preRegisterUser(@RequestBody User user) {
        try {
            User createdUser = userService.preRegisterUser(user);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (DataConflictException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (user.getRole().equals(RoleType.PACIENTE)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            User createdUser = userService.createUser(user);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (DataConflictException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        if (user.getRole().equals(RoleType.PACIENTE)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        User updatedUser = userService.updateUser(id, user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.findUserById(id);
        if (user.getRole().equals(RoleType.PACIENTE)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<User> usersPage = (Page<User>) userService.findAllUsers(PageRequest.of(page, size));
        return new ResponseEntity<>(usersPage, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody UserRequestDTO userRequestDTO) {
        try {
            String jwt = userService.loginUser(userRequestDTO.getEmail(), userRequestDTO.getPassword());
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/email/{email}/redefinir-senha")
    public ResponseEntity<Void> resetPassword(@PathVariable String email, @RequestBody String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        userService.resetPassword(email, newPassword);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}