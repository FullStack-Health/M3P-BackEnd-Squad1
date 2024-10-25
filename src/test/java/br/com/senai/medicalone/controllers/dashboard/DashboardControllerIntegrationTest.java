package br.com.senai.medicalone.controllers.dashboard;

import br.com.senai.medicalone.dtos.dashboard.DashboardMetricsDTO;
import br.com.senai.medicalone.entities.user.RoleType;
import br.com.senai.medicalone.entities.user.User;
import br.com.senai.medicalone.repositories.user.UserRepository;
import br.com.senai.medicalone.services.dashboard.DashboardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Map;

import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class DashboardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String jwtToken;

    @BeforeEach
    public void setUp() throws Exception {
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

    @Test
    public void testGetDashboardData_Success() throws Exception {
        Map<String, Long> statistics = Map.of(
                "appointmentCount", 4L,
                "userCount", 7L,
                "patientCount", 1L,
                "examCount", 2L
        );
        DashboardMetricsDTO mockMetrics = new DashboardMetricsDTO(statistics);

        when(dashboardService.generateDashboardMetrics()).thenReturn(mockMetrics);

        mockMvc.perform(get("/api/dashboard")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Dados do dashboard obtidos com sucesso"))
                .andExpect(jsonPath("$.data.statistics.appointmentCount").value(4))
                .andExpect(jsonPath("$.data.statistics.userCount").value(7))
                .andExpect(jsonPath("$.data.statistics.patientCount").value(1))
                .andExpect(jsonPath("$.data.statistics.examCount").value(2));
    }

    @Test
    public void testGetDashboardData_InternalServerError() throws Exception {
        doThrow(new RuntimeException("Simulated service error")).when(dashboardService).generateDashboardMetrics();

        mockMvc.perform(get("/api/dashboard")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Erro ao obter dados do dashboard"));
    }
}