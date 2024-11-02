package br.com.senai.medicalone.controllers.patient;

import br.com.senai.medicalone.dtos.patient.PatientRequestDTO;
import br.com.senai.medicalone.dtos.patient.PatientResponseDTO;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        patientRepository.deleteAll();
        userRepository.deleteAll();

        createUserAndAuthenticate();
        patientRequestDTO = createMockPatient();
    }

    private void createUserAndAuthenticate() throws Exception {
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
        patientRequestDTO.setZipCode("12345-678");
        patientRequestDTO.setCity("São Paulo");
        patientRequestDTO.setState("SP");
        patientRequestDTO.setStreet("Rua Exemplo");
        patientRequestDTO.setNumber("123");
        patientRequestDTO.setComplement("Apto 101");
        patientRequestDTO.setNeighborhood("Centro");
        patientRequestDTO.setReferencePoint("Próximo ao mercado");
        return patientRequestDTO;
    }

    @Test
    public void testCreatePatient_Success() throws Exception {
        mockMvc.perform(post("/api/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientRequestDTO))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Paciente criado com sucesso"))
                .andExpect(jsonPath("$.patient.fullName").value("John Doe"))
                .andExpect(jsonPath("$.patient.gender").value("Masculino"))
                .andExpect(jsonPath("$.patient.birthDate").value("1990-01-01"))
                .andExpect(jsonPath("$.patient.cpf").value("12345678945"))
                .andExpect(jsonPath("$.patient.rg").value("1234567890"))
                .andExpect(jsonPath("$.patient.rgIssuer").value("SSP"))
                .andExpect(jsonPath("$.patient.maritalStatus").value("Solteiro"))
                .andExpect(jsonPath("$.patient.phone").value("99999999999"))
                .andExpect(jsonPath("$.patient.email").value("johndoe@example.com"))
                .andExpect(jsonPath("$.patient.placeOfBirth").value("São Paulo"))
                .andExpect(jsonPath("$.patient.emergencyContact").value("99999999999"))
                .andExpect(jsonPath("$.patient.allergies[0]").value("Poeira"))
                .andExpect(jsonPath("$.patient.allergies[1]").value("Amendoim"))
                .andExpect(jsonPath("$.patient.specificCare[0]").value("Precisa de acompanhamento cardíaco"))
                .andExpect(jsonPath("$.patient.healthInsurance").value("Unimed"))
                .andExpect(jsonPath("$.patient.healthInsuranceNumber").value("1234567890"))
                .andExpect(jsonPath("$.patient.healthInsuranceValidity").value("2025-12-31"))
                .andExpect(jsonPath("$.patient.zipCode").value("12345678"))
                .andExpect(jsonPath("$.patient.city").value("São Paulo"))
                .andExpect(jsonPath("$.patient.state").value("SP"))
                .andExpect(jsonPath("$.patient.street").value("Rua Exemplo"))
                .andExpect(jsonPath("$.patient.number").value("123"))
                .andExpect(jsonPath("$.patient.complement").value("Apto 101"))
                .andExpect(jsonPath("$.patient.neighborhood").value("Centro"))
                .andExpect(jsonPath("$.patient.referencePoint").value("Próximo ao mercado"));
    }

    @Test
    public void testCreatePatient_Conflict() throws Exception {
        patientService.createPatient(patientRequestDTO);

        mockMvc.perform(post("/api/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientRequestDTO))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Paciente já cadastrado"));
    }

    @Test
    public void testGetPatientById_Success() throws Exception {
        PatientResponseDTO savedPatient = patientService.createPatient(patientRequestDTO);

        mockMvc.perform(get("/api/pacientes/{id}", savedPatient.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Paciente encontrado com sucesso"))
                .andExpect(jsonPath("$.patient.fullName").value("John Doe"));
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
                .andExpect(jsonPath("$.message").value("Paciente atualizado com sucesso"))
                .andExpect(jsonPath("$.patient.fullName").value("Jane Doe"));
    }

    @Test
    public void testDeletePatient_Success() throws Exception {
        PatientResponseDTO savedPatient = patientService.createPatient(patientRequestDTO);

        mockMvc.perform(delete("/api/pacientes/{id}", savedPatient.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Paciente excluído com sucesso"));
    }

    @Test
    public void testGetAllPatients_Success() throws Exception {
        patientService.createPatient(patientRequestDTO);
        mockMvc.perform(get("/api/pacientes")
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Pacientes encontrados com sucesso"))
                .andExpect(jsonPath("$.patients[0].fullName").value("John Doe"))
                .andExpect(jsonPath("$.page.size").value(10))
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.page.totalPages").value(1))
                .andExpect(jsonPath("$.page.number").value(0));
    }

    @Test
    public void testGetPatientByCpf_Success() throws Exception {
        PatientResponseDTO savedPatient = patientService.createPatient(patientRequestDTO);

        mockMvc.perform(get("/api/pacientes/cpf/{cpf}", savedPatient.getCpf())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Paciente encontrado com sucesso"))
                .andExpect(jsonPath("$.patient.cpf").value(savedPatient.getCpf()));
    }

    @Test
    public void testGetPatientsByName_Success() throws Exception {
        patientService.createPatient(patientRequestDTO);

        mockMvc.perform(get("/api/pacientes/nome/{name}", patientRequestDTO.getFullName())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Pacientes encontrados com sucesso"))
                .andExpect(jsonPath("$.patients[0].fullName").value(patientRequestDTO.getFullName()));
    }

    @Test
    public void testGetPatientsByPhone_Success() throws Exception {
        PatientResponseDTO savedPatient = patientService.createPatient(patientRequestDTO);
        assertNotNull(savedPatient);
        assertEquals(patientRequestDTO.getPhone().replaceAll("[^0-9]", ""), savedPatient.getPhone());
        mockMvc.perform(get("/api/pacientes/telefone/{phone}", patientRequestDTO.getPhone().replaceAll("[^0-9]", ""))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Pacientes encontrados com sucesso"))
                .andExpect(jsonPath("$.patients[0].phone").value(patientRequestDTO.getPhone().replaceAll("[^0-9]", "")));
    }

    @Test
    public void testCreatePatient_MissingData() throws Exception {
        PatientRequestDTO invalidPatientRequestDTO = new PatientRequestDTO();
        mockMvc.perform(post("/api/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPatientRequestDTO))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Dados ausentes ou incorretos"));
    }

    @Test
    public void testGetPatientById_NotFound() throws Exception {
        mockMvc.perform(get("/api/pacientes/{id}", 999L)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Paciente não encontrado"));
    }

    @Test
    public void testUpdatePatient_InvalidData() throws Exception {
        PatientResponseDTO savedPatient = patientService.createPatient(patientRequestDTO);
        patientRequestDTO.setFullName(""); // Invalid data

        mockMvc.perform(put("/api/pacientes/{id}", savedPatient.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientRequestDTO))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Dados ausentes ou incorretos"));
    }

    @Test
    public void testDeletePatient_NotFound() throws Exception {
        mockMvc.perform(delete("/api/pacientes/{id}", 999L)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Paciente não encontrado com ID: 999"));
    }

    @Test
    public void testGetPatientByCpf_NotFound() throws Exception {
        mockMvc.perform(get("/api/pacientes/cpf/{cpf}", "000.000.000-00")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Paciente não encontrado"));
    }

    @Test
    public void testGetPatientsByName_NotFound() throws Exception {
        mockMvc.perform(get("/api/pacientes/nome/{name}", "NonExistentName")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Pacientes não encontrados"));
    }

    @Test
    public void testGetPatientsByPhone_NotFound() throws Exception {
        mockMvc.perform(get("/api/pacientes/telefone/{phone}", "0000000000")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Pacientes não encontrados"));
    }
}