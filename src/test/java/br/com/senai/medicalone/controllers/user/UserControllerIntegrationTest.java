package br.com.senai.medicalone.controllers.user;

import br.com.senai.medicalone.entities.user.RoleType;
import br.com.senai.medicalone.entities.user.User;
import br.com.senai.medicalone.repositories.patient.PatientRepository;
import br.com.senai.medicalone.repositories.user.UserRepository;
import br.com.senai.medicalone.repositories.user.PreRegisterUserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PreRegisterUserRepository preRegisterUserRepository;

    @BeforeEach
    public void setup() {
        patientRepository.deleteAll();
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
                .andExpect(jsonPath("$.message").value("Usuário pré-cadastrado com sucesso"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testCreateUser_Success() throws Exception {
        String userJson = "{\"name\":\"John Doe\",\"email\":\"john.doe@example.com\",\"birthDate\":\"1990-01-01\",\"phone\":\"1234567890\",\"cpf\":\"12345678900\",\"password\":\"password123\",\"role\":\"ADMIN\"}";

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Usuário criado com sucesso"))
                .andExpect(jsonPath("$.user.email").value("john.doe@example.com"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdateUser_Success() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(RoleType.ADMIN);
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setCpf("12345678900");
        user.setName("Test User");
        user.setPhone("1234567890");
        userRepository.save(user);

        String updatedUserJson = "{\"name\":\"John Doe Updated\",\"email\":\"updateduser@example.com\",\"birthDate\":\"1990-01-01\",\"phone\":\"1234567890\",\"cpf\":\"12345678900\",\"password\":\"newpassword123\",\"role\":\"ADMIN\"}";

        mockMvc.perform(put("/api/usuarios/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedUserJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuário atualizado com sucesso"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testDeleteUser_Success() throws Exception {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(RoleType.ADMIN);
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setCpf("12345678900");
        user.setName("Test User");
        user.setPhone("1234567890");
        userRepository.save(user);

        String loginJson = "{\"email\":\"test@example.com\",\"password\":\"password\"}";

        String responseContent = mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseContent);
        String token = jsonNode.get("token").asText();

        mockMvc.perform(delete("/api/usuarios/" + user.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuário excluído com sucesso"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetUserById_Success() throws Exception {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(RoleType.ADMIN);
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setCpf("12345678900");
        user.setName("Test User");
        user.setPhone("1234567890");
        userRepository.save(user);

        String loginJson = "{\"email\":\"test@example.com\",\"password\":\"password\"}";

        String responseContent = mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseContent);
        String token = jsonNode.get("token").asText();

        mockMvc.perform(get("/api/usuarios/" + user.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuário encontrado com sucesso"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetAllUsers_Success() throws Exception {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(RoleType.ADMIN);
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setCpf("12345678900");
        user.setName("Test User");
        user.setPhone("1234567890");
        userRepository.save(user);

        String loginJson = "{\"email\":\"test@example.com\",\"password\":\"password\"}";

        String responseContent = mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseContent);
        String token = jsonNode.get("token").asText();

        mockMvc.perform(get("/api/usuarios")
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuários encontrados com sucesso"))
                .andExpect(jsonPath("$.users[0].email").value("test@example.com"))
                .andExpect(jsonPath("$.page.size").value(10))
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.page.totalPages").value(1))
                .andExpect(jsonPath("$.page.number").value(0));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testLoginUser_Success() throws Exception {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(RoleType.ADMIN);
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setCpf("12345678900");
        user.setName("Test User");
        user.setPhone("1234567890");
        userRepository.save(user);

        String loginJson = "{\"email\":\"test@example.com\",\"password\":\"password\"}";

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testResetPassword_Success() throws Exception {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(RoleType.ADMIN);
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setCpf("12345678900");
        user.setName("Test User");
        user.setPhone("1234567890");
        userRepository.save(user);

        String loginJson = "{\"email\":\"test@example.com\",\"password\":\"password\"}";
        String responseContent = mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseContent);
        String token = jsonNode.get("token").asText();

        String resetPasswordJson = "{\"newPassword\":\"newpassword\"}";

        mockMvc.perform(put("/api/usuarios/email/test@example.com/redefinir-senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resetPasswordJson)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Senha redefinida com sucesso"));
    }

    @Test
    public void testPreRegisterUser_EmailAlreadyExists() throws Exception {
        String preRegisterUserJson = "{\"email\":\"preuser@example.com\",\"password\":\"password\",\"role\":\"ADMIN\"}";

        mockMvc.perform(post("/api/usuarios/pre-registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(preRegisterUserJson))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/usuarios/pre-registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(preRegisterUserJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email já cadastrado."));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testCreateUser_EmailAlreadyExists() throws Exception {
        String userJson = "{\"name\":\"John Doe\",\"email\":\"john.doe@example.com\",\"birthDate\":\"1990-01-01\",\"phone\":\"1234567890\",\"cpf\":\"12345678900\",\"password\":\"password123\",\"role\":\"ADMIN\"}";

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email já cadastrado."));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdateUser_UserNotFound() throws Exception {
        String updatedUserJson = "{\"name\":\"John Doe Updated\",\"email\":\"updateduser@example.com\",\"birthDate\":\"1990-01-01\",\"phone\":\"1234567890\",\"cpf\":\"12345678900\",\"password\":\"newpassword123\",\"role\":\"ADMIN\"}";

        mockMvc.perform(put("/api/usuarios/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedUserJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Usuário não encontrado"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testDeleteUser_UserNotFound() throws Exception {
        mockMvc.perform(delete("/api/usuarios/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Usuário não encontrado"));
    }

    @Test
    public void testLoginUser_InvalidCredentials() throws Exception {
        String loginJson = "{\"email\":\"invalid@example.com\",\"password\":\"wrongpassword\"}";

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenciais inválidas"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testResetPassword_EmailNotFound() throws Exception {
        String resetPasswordJson = "{\"newPassword\":\"newpassword\"}";

        mockMvc.perform(put("/api/usuarios/email/nonexistent@example.com/redefinir-senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resetPasswordJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Usuário não encontrado"));
    }
}