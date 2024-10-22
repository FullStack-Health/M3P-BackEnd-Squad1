package br.com.senai.medicalone.services.dashboard;

import br.com.senai.medicalone.dtos.dashboard.DashboardMetricsDTO;
import br.com.senai.medicalone.mappers.dashboard.DashboardMetricsMapper;
import br.com.senai.medicalone.repositories.appointment.AppointmentRepository;
import br.com.senai.medicalone.repositories.exam.ExamRepository;
import br.com.senai.medicalone.repositories.patient.PatientRepository;
import br.com.senai.medicalone.repositories.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DashboardMetricsMapper dashboardMetricsMapper;

    @Operation(summary = "Generate dashboard metrics", description = "Método para gerar métricas do dashboard")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Métricas geradas com sucesso")
    })
    public DashboardMetricsDTO generateDashboardMetrics() {
        Map<String, Long> statistics = new HashMap<>();

        statistics.put("patientCount", patientRepository.count());
        statistics.put("appointmentCount", appointmentRepository.count());
        statistics.put("examCount", examRepository.count());
        statistics.put("userCount", userRepository.count());

        return dashboardMetricsMapper.toDTO(statistics);
    }
}