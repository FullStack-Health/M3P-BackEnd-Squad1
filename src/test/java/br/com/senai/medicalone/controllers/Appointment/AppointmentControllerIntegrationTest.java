package br.com.senai.medicalone.controllers.Appointment;

import br.com.senai.medicalone.dtos.appointment.AppointmentRequestDTO;
import br.com.senai.medicalone.dtos.appointment.AppointmentResponseDTO;
import br.com.senai.medicalone.dtos.patient.PatientRequestDTO;
import br.com.senai.medicalone.entities.user.RoleType;
import br.com.senai.medicalone.entities.user.User;
import br.com.senai.medicalone.repositories.patient.PatientRepository;
import br.com.senai.medicalone.repositories.user.UserRepository;
import br.com.senai.medicalone.services.appointment.AppointmentService;
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
public class AppointmentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private AppointmentRequestDTO appointmentRequestDTO;

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

    private AppointmentRequestDTO createMockAppointment(Long patientId) {
        AppointmentRequestDTO appointmentRequestDTO = new AppointmentRequestDTO();
        appointmentRequestDTO.setAppointmentReason("Consulta de rotina");
        appointmentRequestDTO.setAppointmentDate(LocalDate.of(2023, 10, 1));
        appointmentRequestDTO.setAppointmentTime(LocalTime.of(10, 0));
        appointmentRequestDTO.setProblemDescription("Paciente vem para uma consulta de rotina.");
        appointmentRequestDTO.setPrescribedMedication("Nenhuma");
        appointmentRequestDTO.setObservations("Paciente deve retornar em seis meses.");
        appointmentRequestDTO.setPatientId(patientId);
        return appointmentRequestDTO;
    }

    @Test
    public void testCreateAppointment_Success() throws Exception {
        Long patientId = createMockPatient();
        appointmentRequestDTO = createMockAppointment(patientId);

        mockMvc.perform(post("/api/consultas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointmentRequestDTO))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Consulta criada com sucesso"))
                .andExpect(jsonPath("$.appointment.appointmentReason").value(appointmentRequestDTO.getAppointmentReason()));
    }

    @Test
    public void testCreateAppointment_MissingData() throws Exception {
        AppointmentRequestDTO invalidAppointmentRequestDTO = new AppointmentRequestDTO();
        mockMvc.perform(post("/api/consultas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidAppointmentRequestDTO))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Motivo da consulta é obrigatório"));
    }

    @Test
    public void testGetAppointmentById_Success() throws Exception {
        Long patientId = createMockPatient();
        appointmentRequestDTO = createMockAppointment(patientId);
        AppointmentResponseDTO savedAppointment = appointmentService.createAppointment(appointmentRequestDTO);

        mockMvc.perform(get("/api/consultas/{id}", savedAppointment.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Consulta encontrada com sucesso"))
                .andExpect(jsonPath("$.appointment.appointmentReason").value(savedAppointment.getAppointmentReason()));
    }

    @Test
    public void testGetAppointmentById_NotFound() throws Exception {
        mockMvc.perform(get("/api/consultas/{id}", 999L)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Consulta não encontrada"));
    }

    @Test
    public void testUpdateAppointment_Success() throws Exception {
        Long patientId = createMockPatient();
        appointmentRequestDTO = createMockAppointment(patientId);
        AppointmentResponseDTO savedAppointment = appointmentService.createAppointment(appointmentRequestDTO);
        appointmentRequestDTO.setAppointmentReason("Updated Reason");

        mockMvc.perform(put("/api/consultas/{id}", savedAppointment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointmentRequestDTO))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Consulta atualizada com sucesso"))
                .andExpect(jsonPath("$.appointment.appointmentReason").value("Updated Reason"));
    }

    @Test
    public void testUpdateAppointment_InvalidData() throws Exception {
        Long patientId = createMockPatient();
        appointmentRequestDTO = createMockAppointment(patientId);
        AppointmentResponseDTO savedAppointment = appointmentService.createAppointment(appointmentRequestDTO);
        appointmentRequestDTO.setAppointmentReason("");

        mockMvc.perform(put("/api/consultas/{id}", savedAppointment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointmentRequestDTO))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteAppointment_Success() throws Exception {
        Long patientId = createMockPatient();
        appointmentRequestDTO = createMockAppointment(patientId);
        AppointmentResponseDTO savedAppointment = appointmentService.createAppointment(appointmentRequestDTO);

        mockMvc.perform(delete("/api/consultas/{id}", savedAppointment.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Consulta excluída com sucesso"));
    }

    @Test
    public void testDeleteAppointment_NotFound() throws Exception {
        mockMvc.perform(delete("/api/consultas/{id}", 999L)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Consulta não encontrada"));
    }

    @Test
    public void testListAppointments_Success() throws Exception {
        Long patientId = createMockPatient();
        appointmentRequestDTO = createMockAppointment(patientId);
        appointmentService.createAppointment(appointmentRequestDTO);

        mockMvc.perform(get("/api/consultas")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Consultas encontradas com sucesso"))
                .andExpect(jsonPath("$.appointments[0].appointmentReason").value(appointmentRequestDTO.getAppointmentReason()))
                .andExpect(jsonPath("$.page.size").value(12))
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.page.totalPages").value(1))
                .andExpect(jsonPath("$.page.number").value(0));
    }
}