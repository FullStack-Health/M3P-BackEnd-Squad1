package br.com.senai.medicalone.controllers.exam;

import br.com.senai.medicalone.dtos.exam.ExamRequestDTO;
import br.com.senai.medicalone.dtos.exam.ExamResponseDTO;
import br.com.senai.medicalone.dtos.patient.PatientRequestDTO;
import br.com.senai.medicalone.entities.user.RoleType;
import br.com.senai.medicalone.entities.user.User;
import br.com.senai.medicalone.repositories.patient.PatientRepository;
import br.com.senai.medicalone.repositories.user.UserRepository;
import br.com.senai.medicalone.services.exam.ExamService;
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
import java.time.LocalTime;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ExamControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExamService examService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private ExamRequestDTO examRequestDTO;

    private String jwtToken;

    @BeforeEach
    public void setUp() throws Exception {
        patientRepository.deleteAll();
        userRepository.deleteAll();

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

    private Long createMockPatient() throws Exception {
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

        String response = mockMvc.perform(post("/api/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientRequestDTO))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("patient").get("id").asLong();
    }

    private ExamRequestDTO createMockExam(Long patientId) {
        ExamRequestDTO examRequestDTO = new ExamRequestDTO();
        examRequestDTO.setName("Hemograma Completo");
        examRequestDTO.setExamDate(LocalDate.of(2023, 10, 1));
        examRequestDTO.setExamTime(LocalTime.of(8, 30));
        examRequestDTO.setType("Sangue");
        examRequestDTO.setLaboratory("Laboratório XYZ");
        examRequestDTO.setDocumentUrl("http://example.com/document.pdf");
        examRequestDTO.setResults("Resultados detalhados do exame");
        examRequestDTO.setPatientId(patientId);
        return examRequestDTO;
    }

    @Test
    public void testCreateExam_Success() throws Exception {
        Long patientId = createMockPatient();
        examRequestDTO = createMockExam(patientId);

        mockMvc.perform(post("/api/exames")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(examRequestDTO))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Exame criado com sucesso"))
                .andExpect(jsonPath("$.exam.name").value(examRequestDTO.getName()));
    }

    @Test
    public void testCreateExam_MissingData() throws Exception {
        ExamRequestDTO invalidExamRequestDTO = new ExamRequestDTO();
        mockMvc.perform(post("/api/exames")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidExamRequestDTO))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Erro ao criar exame"));
    }

    @Test
    public void testGetExamById_Success() throws Exception {
        Long patientId = createMockPatient();
        examRequestDTO = createMockExam(patientId);
        ExamResponseDTO savedExam = examService.createExam(examRequestDTO);

        mockMvc.perform(get("/api/exames/{id}", savedExam.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Exame encontrado com sucesso"))
                .andExpect(jsonPath("$.exam.name").value(savedExam.getName()));
    }

    @Test
    public void testGetExamById_NotFound() throws Exception {
        mockMvc.perform(get("/api/exames/{id}", 999L)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Exame não encontrado"));
    }

    @Test
    public void testUpdateExam_Success() throws Exception {
        Long patientId = createMockPatient();
        examRequestDTO = createMockExam(patientId);
        ExamResponseDTO savedExam = examService.createExam(examRequestDTO);
        examRequestDTO.setName("Updated Exam Name");

        mockMvc.perform(put("/api/exames/{id}", savedExam.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(examRequestDTO))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Exame atualizado com sucesso"))
                .andExpect(jsonPath("$.exam.name").value("Updated Exam Name"));
    }

    @Test
    public void testUpdateExam_InvalidData() throws Exception {
        Long patientId = createMockPatient();
        examRequestDTO = createMockExam(patientId);
        ExamResponseDTO savedExam = examService.createExam(examRequestDTO);
        examRequestDTO.setName(""); // Invalid data

        mockMvc.perform(put("/api/exames/{id}", savedExam.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(examRequestDTO))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Erro ao atualizar exame"));
    }

    @Test
    public void testDeleteExam_Success() throws Exception {
        Long patientId = createMockPatient();
        examRequestDTO = createMockExam(patientId);
        ExamResponseDTO savedExam = examService.createExam(examRequestDTO);

        mockMvc.perform(delete("/api/exames/{id}", savedExam.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Exame excluído com sucesso"));
    }

    @Test
    public void testDeleteExam_NotFound() throws Exception {
        mockMvc.perform(delete("/api/exames/{id}", 999L)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Exame não encontrado"));
    }

    @Test
    public void testListExams_Success() throws Exception {
        Long patientId = createMockPatient();
        examRequestDTO = createMockExam(patientId);
        examService.createExam(examRequestDTO);

        mockMvc.perform(get("/api/exames")
                        .param("size", "12")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Exames encontrados com sucesso"))
                .andExpect(jsonPath("$.exams[0].name").value(examRequestDTO.getName()))
                .andExpect(jsonPath("$.page.size").value(12))
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.page.totalPages").value(1))
                .andExpect(jsonPath("$.page.number").value(0));
    }
}