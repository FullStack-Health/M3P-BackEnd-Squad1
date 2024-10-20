package br.com.senai.medicalone.controllers.patient;

import br.com.senai.medicalone.dtos.patient.PatientRequestDTO;
import br.com.senai.medicalone.dtos.patient.PatientResponseDTO;
import br.com.senai.medicalone.entities.patient.Address;
import br.com.senai.medicalone.entities.user.RoleType;
import br.com.senai.medicalone.entities.user.User;
import br.com.senai.medicalone.repositories.patient.PatientRepository;
import br.com.senai.medicalone.repositories.user.UserRepository;
import br.com.senai.medicalone.services.patient.PatientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PatientControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientService patientService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    private PatientRequestDTO patientRequestDTO;

    private String jwtToken;

    @BeforeEach
    public void setUp() throws Exception {
        userRepository.deleteAll();
        patientRepository.deleteAll();

        User user = new User();
        user.setEmail("admin@example.com");
        user.setPassword(passwordEncoder.encode("adminpassword"));
        user.setRole(RoleType.ADMIN);
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setCpf("123.456.789-00");
        user.setName("Admin User");
        user.setPhone("1234567890");
        userRepository.save(user);

        String loginJson = "{\"email\":\"admin@example.com\",\"password\":\"adminpassword\"}";
        String response = mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        jwtToken = objectMapper.readTree(response).get("token").asText();

        patientRequestDTO = createMockPatient();
    }

    private PatientRequestDTO createMockPatient() {
        PatientRequestDTO patientRequestDTO = new PatientRequestDTO();
        patientRequestDTO.setFullName("John Doe");
        patientRequestDTO.setGender("Masculino");
        patientRequestDTO.setBirthDate(LocalDate.parse("1990-01-01"));
        patientRequestDTO.setCpf("123.456.789-45");
        patientRequestDTO.setRg("1234567890");
        patientRequestDTO.setRgIssuer("SSP");
        patientRequestDTO.setMaritalStatus("Solteiro");
        patientRequestDTO.setPhone("(99) 9 9999-9999");
        patientRequestDTO.setEmail("johndoe@example.com");
        patientRequestDTO.setPlaceOfBirth("São Paulo");
        patientRequestDTO.setEmergencyContact("(99) 9 9999-9999");
        patientRequestDTO.setAllergies(Arrays.asList("Poeira", "Amendoim"));
        patientRequestDTO.setSpecificCare(Arrays.asList("Precisa de acompanhamento cardíaco"));
        patientRequestDTO.setHealthInsurance("Unimed");
        patientRequestDTO.setHealthInsuranceNumber("1234567890");
        patientRequestDTO.setHealthInsuranceValidity(LocalDate.parse("2025-12-31"));
        patientRequestDTO.setAddress(new Address("12345-678", "São Paulo", "SP", "Rua Exemplo", "123", "Apto 101", "Centro", "Próximo ao mercado"));
        return patientRequestDTO;
    }

    @Test
    public void testCreatePatient_Success() throws Exception {
        mockMvc.perform(post("/api/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientRequestDTO))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.gender").value("Masculino"))
                .andExpect(jsonPath("$.birthDate").value("1990-01-01"))
                .andExpect(jsonPath("$.cpf").value("12345678945"))
                .andExpect(jsonPath("$.rg").value("1234567890"))
                .andExpect(jsonPath("$.rgIssuer").value("SSP"))
                .andExpect(jsonPath("$.maritalStatus").value("Solteiro"))
                .andExpect(jsonPath("$.phone").value("99999999999"))
                .andExpect(jsonPath("$.email").value("johndoe@example.com"))
                .andExpect(jsonPath("$.placeOfBirth").value("São Paulo"))
                .andExpect(jsonPath("$.emergencyContact").value("99999999999"))
                .andExpect(jsonPath("$.allergies[0]").value("Poeira"))
                .andExpect(jsonPath("$.allergies[1]").value("Amendoim"))
                .andExpect(jsonPath("$.specificCare[0]").value("Precisa de acompanhamento cardíaco"))
                .andExpect(jsonPath("$.healthInsurance").value("Unimed"))
                .andExpect(jsonPath("$.healthInsuranceNumber").value("1234567890"))
                .andExpect(jsonPath("$.healthInsuranceValidity").value("2025-12-31"))
                .andExpect(jsonPath("$.address.zipCode").value("12345678"))
                .andExpect(jsonPath("$.address.city").value("São Paulo"))
                .andExpect(jsonPath("$.address.state").value("SP"))
                .andExpect(jsonPath("$.address.street").value("Rua Exemplo"))
                .andExpect(jsonPath("$.address.number").value("123"))
                .andExpect(jsonPath("$.address.complement").value("Apto 101"))
                .andExpect(jsonPath("$.address.neighborhood").value("Centro"))
                .andExpect(jsonPath("$.address.referencePoint").value("Próximo ao mercado"));
    }

    @Test
    public void testGetPatientById_Success() throws Exception {
        PatientResponseDTO savedPatient = patientService.createPatient(patientRequestDTO);

        mockMvc.perform(get("/api/pacientes/{id}", savedPatient.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("John Doe"));
    }

    @Test
    public void testUpdatePatient_Success() throws Exception {
        PatientResponseDTO savedPatient = patientService.createPatient(patientRequestDTO);
        patientRequestDTO.setFullName("Jane Doe");

        mockMvc.perform(put("/api/pacientes/{id}", savedPatient.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientRequestDTO))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Jane Doe"));
    }

    @Test
    public void testDeletePatient_Success() throws Exception {
        PatientResponseDTO savedPatient = patientService.createPatient(patientRequestDTO);

        mockMvc.perform(delete("/api/pacientes/{id}", savedPatient.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("paciente excluido com sucesso"));
    }

    @Test
    public void testGetAllPatients_Success() throws Exception {
        patientService.createPatient(patientRequestDTO);
        mockMvc.perform(get("/api/pacientes")
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].fullName").value("John Doe"));
    }
}