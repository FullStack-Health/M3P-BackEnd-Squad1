package br.com.senai.medicalone.services.patient;

import br.com.senai.medicalone.dtos.appointment.AppointmentResponseDTO;
import br.com.senai.medicalone.dtos.patient.PatientRecordDTO;
import br.com.senai.medicalone.entities.patient.Patient;
import br.com.senai.medicalone.exceptions.customexceptions.PatientNotFoundException;
import br.com.senai.medicalone.mappers.patient.PatientRecordMapper;
import br.com.senai.medicalone.repositories.patient.PatientRepository;
import br.com.senai.medicalone.services.exam.ExamService;
import br.com.senai.medicalone.services.appointment.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientRecordService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ExamService examService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PatientRecordMapper patientRecordMapper;

    @Operation(summary = "Obter prontuário do paciente", description = "Método para obter o prontuário de um paciente pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Prontuário encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado")
    })
    public PatientRecordDTO getPatientRecord(Long patientId) {
        var patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Paciente não encontrado com ID: " + patientId));
        var exams = examService.getExamsByPatientId(patientId, Pageable.unpaged()).getContent();
        var appointments = appointmentService.getAppointmentsByPatientId(patientId, Pageable.unpaged()).getContent();
        return patientRecordMapper.toDTO(patient, exams, appointments);
    }

    @Operation(summary = "Obter todos os prontuários de pacientes", description = "Método para obter todos os prontuários de pacientes com paginação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Prontuários encontrados com sucesso")
    })
    public Page<PatientRecordDTO> getAllPatientRecords(String name, Long id, Pageable pageable) {
        Page<Patient> patients = patientRepository.findAll(pageable);
        List<PatientRecordDTO> records = patients.stream()
                .map(patient -> {
                    var exams = examService.getExamsByPatientId(patient.getId(), Pageable.unpaged()).getContent();
                    var appointments = appointmentService.getAppointmentsByPatientId(patient.getId(), Pageable.unpaged()).getContent();
                    return patientRecordMapper.toDTO(patient, exams, appointments);
                })
                .collect(Collectors.toList());
        return new PageImpl<>(records, pageable, patients.getTotalElements());
    }
}