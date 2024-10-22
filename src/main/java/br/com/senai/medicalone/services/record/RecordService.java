package br.com.senai.medicalone.services.record;

import br.com.senai.medicalone.dtos.record.RecordResponseDTO;
import br.com.senai.medicalone.dtos.patient.PatientResponseDTO;
import br.com.senai.medicalone.entities.record.Record;
import br.com.senai.medicalone.entities.patient.Patient;
import br.com.senai.medicalone.exceptions.customexceptions.RecordNotFoundException;
import br.com.senai.medicalone.exceptions.customexceptions.PatientNotFoundException;
import br.com.senai.medicalone.mappers.appointment.AppointmentMapper;
import br.com.senai.medicalone.mappers.exam.ExamMapper;
import br.com.senai.medicalone.mappers.patient.PatientMapper;
import br.com.senai.medicalone.repositories.record.RecordRepository;
import br.com.senai.medicalone.repositories.patient.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecordService {

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    public List<PatientResponseDTO> listPatients(String nome, Long id, Pageable pageable) {
        if (id != null) {
            Patient patient = patientRepository.findById(id)
                    .orElseThrow(() -> new PatientNotFoundException("Paciente não encontrado"));
            return List.of(patientMapper.toResponseDTO(patient));
        }

        Page<Patient> patientPage;

        if (nome != null) {
            patientPage = patientRepository.findByName(nome, pageable);
        } else {
            patientPage = patientRepository.findAll(pageable);
        }
        return patientPage.stream()
                .map(patientMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public RecordResponseDTO getChartByPatientId(Long patientId) {
        Optional<Patient> patientOptional = patientRepository.findById(patientId);
        if (patientOptional.isEmpty()) {
            throw new PatientNotFoundException("Paciente não encontrado");
        }
        Record record = recordRepository.findByPatient(patientOptional.get());
        if (record == null) {
            throw new RecordNotFoundException("Prontuário não encontrado para o paciente");
        }
        return convertToResponseDTO(record);
    }
    private RecordResponseDTO convertToResponseDTO(Record record) {
        RecordResponseDTO responseDTO = new RecordResponseDTO();
        responseDTO.setPatient(patientMapper.toResponseDTO(record.getPatient()));
        responseDTO.setExams(record.getExams().stream().map(examMapper::toResponseDTO).toList());
        responseDTO.setAppointments(record.getAppointments().stream().map(appointmentMapper::toResponseDTO).toList());
        return responseDTO;
    }
}