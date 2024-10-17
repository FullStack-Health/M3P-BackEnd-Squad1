package br.com.senai.medicalone.controllers;

import br.com.senai.medicalone.entities.RoleType;
import br.com.senai.medicalone.entities.User;
import br.com.senai.medicalone.repositories.UserRepository;
import br.com.senai.medicalone.repositories.PreRegisterUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PreRegisterUserRepository preRegisterUserRepository;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        preRegisterUserRepository.deleteAll();
    }

    @Test
    public void testPreRegisterUser_Success() throws Exception {
        String preRegisterUserJson = "{\"email\":\"preuser@example.com\",\"password\":\"password\",\"role\":\"ADMIN\"}";

        mockMvc.perform(post("/api/usuarios/pre-registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(preRegisterUserJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("preuser@example.com"));
    }

    @Test
    public void testCreateUser_Success() throws Exception {
        String userJson = "{\"name\":\"John Doe\",\"email\":\"john.doe@example.com\",\"birthDate\":\"1990-01-01\",\"phone\":\"1234567890\",\"cpf\":\"123.456.789-00\",\"password\":\"password123\",\"role\":\"ADMIN\"}";

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    public void testUpdateUser_Success() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(RoleType.ADMIN);
        userRepository.save(user);

        String updatedUserJson = "{\"email\":\"updated@example.com\",\"name\":\"Updated Name\",\"role\":\"ADMIN\"}";

        mockMvc.perform(put("/api/usuarios/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedUserJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    public void testDeleteUser_Success() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(RoleType.ADMIN);
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setCpf("123.456.789-00");
        userRepository.save(user);

        mockMvc.perform(delete("/api/usuarios/" + user.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetUserById_Success() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(RoleType.ADMIN);
        userRepository.save(user);

        mockMvc.perform(get("/api/usuarios/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    public void testGetAllUsers_Success() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(RoleType.ADMIN);
        userRepository.save(user);

        mockMvc.perform(get("/api/usuarios")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value("test@example.com"));
    }

    @Test
    public void testLoginUser_Success() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(RoleType.ADMIN);
        userRepository.save(user);

        String loginJson = "{\"email\":\"test@example.com\",\"password\":\"password\"}";

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void testResetPassword_Success() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(RoleType.ADMIN);
        userRepository.save(user);

        String resetPasswordJson = "{\"newPassword\":\"newpassword\"}";

        mockMvc.perform(put("/api/usuarios/email/test@example.com/redefinir-senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resetPasswordJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Senha redefinida com sucesso"));
    }
}