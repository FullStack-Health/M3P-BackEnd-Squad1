package br.com.senai.medicalone.services.patient;

import br.com.senai.medicalone.dtos.patient.PatientRecordDTO;
import br.com.senai.medicalone.entities.patient.Patient;
import br.com.senai.medicalone.exceptions.customexceptions.PatientNotFoundException;
import br.com.senai.medicalone.mappers.patient.PatientRecordMapper;
import br.com.senai.medicalone.repositories.patient.PatientRepository;
import br.com.senai.medicalone.services.exam.ExamService;
import br.com.senai.medicalone.services.appointment.AppointmentService;
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

    public PatientRecordDTO getPatientRecord(Long patientId) {
        var patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Paciente não encontrado com ID: " + patientId));
        var exams = examService.getExamsByPatientId(patientId);
        var appointments = appointmentService.getAppointmentsByPatientId(patientId);
        return patientRecordMapper.toDTO(patient, exams, appointments);
    }

    public Page<PatientRecordDTO> getAllPatientRecords(String name, Long id, Pageable pageable) {
        Page<Patient> patients = patientRepository.findAll(pageable);
        List<PatientRecordDTO> records = patients.stream()
                .map(patient -> {
                    var exams = examService.getExamsByPatientId(patient.getId());
                    var appointments = appointmentService.getAppointmentsByPatientId(patient.getId());
                    return patientRecordMapper.toDTO(patient, exams, appointments);
                })
                .collect(Collectors.toList());
        return new PageImpl<>(records, pageable, patients.getTotalElements());
    }
}