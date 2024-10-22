package br.com.senai.medicalone.services.dashboard;

import br.com.senai.medicalone.dtos.dashboard.DashboardMetricsDTO;
import br.com.senai.medicalone.mappers.dashboard.DashboardMetricsMapper;
import br.com.senai.medicalone.repositories.appointment.AppointmentRepository;
import br.com.senai.medicalone.repositories.exam.ExamRepository;
import br.com.senai.medicalone.repositories.patient.PatientRepository;
import br.com.senai.medicalone.repositories.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DashboardServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private ExamRepository examRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DashboardMetricsMapper dashboardMetricsMapper;

    @InjectMocks
    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateDashboardMetrics_Success() {
        when(patientRepository.count()).thenReturn(10L);
        when(appointmentRepository.count()).thenReturn(20L);
        when(examRepository.count()).thenReturn(30L);
        when(userRepository.count()).thenReturn(40L);

        Map<String, Long> statistics = new HashMap<>();
        statistics.put("patientCount", 10L);
        statistics.put("appointmentCount", 20L);
        statistics.put("examCount", 30L);
        statistics.put("userCount", 40L);

        DashboardMetricsDTO expectedDTO = new DashboardMetricsDTO(statistics);
        when(dashboardMetricsMapper.toDTO(statistics)).thenReturn(expectedDTO);

        DashboardMetricsDTO actualDTO = dashboardService.generateDashboardMetrics();

        assertEquals(expectedDTO, actualDTO);
        verify(patientRepository, times(1)).count();
        verify(appointmentRepository, times(1)).count();
        verify(examRepository, times(1)).count();
        verify(userRepository, times(1)).count();
        verify(dashboardMetricsMapper, times(1)).toDTO(statistics);
    }

    @Test
    void generateDashboardMetrics_EmptyStatistics() {
        when(patientRepository.count()).thenReturn(0L);
        when(appointmentRepository.count()).thenReturn(0L);
        when(examRepository.count()).thenReturn(0L);
        when(userRepository.count()).thenReturn(0L);

        Map<String, Long> statistics = new HashMap<>();
        statistics.put("patientCount", 0L);
        statistics.put("appointmentCount", 0L);
        statistics.put("examCount", 0L);
        statistics.put("userCount", 0L);

        DashboardMetricsDTO expectedDTO = new DashboardMetricsDTO(statistics);
        when(dashboardMetricsMapper.toDTO(statistics)).thenReturn(expectedDTO);

        DashboardMetricsDTO actualDTO = dashboardService.generateDashboardMetrics();

        assertEquals(expectedDTO, actualDTO);
        verify(patientRepository, times(1)).count();
        verify(appointmentRepository, times(1)).count();
        verify(examRepository, times(1)).count();
        verify(userRepository, times(1)).count();
        verify(dashboardMetricsMapper, times(1)).toDTO(statistics);
    }

    @Test
    void generateDashboardMetrics_RepositoryException() {
        when(patientRepository.count()).thenThrow(new RuntimeException("Database error"));
        when(appointmentRepository.count()).thenReturn(20L);
        when(examRepository.count()).thenReturn(30L);
        when(userRepository.count()).thenReturn(40L);

        Map<String, Long> statistics = new HashMap<>();
        statistics.put("patientCount", 0L);
        statistics.put("appointmentCount", 20L);
        statistics.put("examCount", 30L);
        statistics.put("userCount", 40L);

        DashboardMetricsDTO expectedDTO = new DashboardMetricsDTO(statistics);
        when(dashboardMetricsMapper.toDTO(statistics)).thenReturn(expectedDTO);

        DashboardMetricsDTO actualDTO = dashboardService.generateDashboardMetrics();

        assertEquals(expectedDTO, actualDTO);
        verify(patientRepository, times(1)).count();
        verify(appointmentRepository, times(1)).count();
        verify(examRepository, times(1)).count();
        verify(userRepository, times(1)).count();
        verify(dashboardMetricsMapper, times(1)).toDTO(statistics);
    }
}