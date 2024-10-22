package br.com.senai.medicalone.services.dashboard;

import br.com.senai.medicalone.repositories.appointment.AppointmentRepository;
import br.com.senai.medicalone.repositories.exam.ExamRepository;
import br.com.senai.medicalone.repositories.patient.PatientRepository;
import br.com.senai.medicalone.repositories.user.UserRepository;
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

    public Map<String, Long> generateDashboardMetrics() {
        Map<String, Long> statistics = new HashMap<>();

        statistics.put("quantidadePacientes", patientRepository.count());
        statistics.put("quantidadeConsultas", appointmentRepository.count());
        statistics.put("quantidadeExames", examRepository.count());
        statistics.put("quantidadeUsuarios", userRepository.count());

        return statistics;
    }
}
