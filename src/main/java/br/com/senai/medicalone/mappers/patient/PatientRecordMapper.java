package br.com.senai.medicalone.mappers.patient;

import br.com.senai.medicalone.dtos.appointment.AppointmentResponseDTO;
import br.com.senai.medicalone.dtos.exam.ExamResponseDTO;
import br.com.senai.medicalone.dtos.patient.PatientRecordDTO;
import br.com.senai.medicalone.dtos.patient.PatientResponseDTO;
import br.com.senai.medicalone.entities.patient.Patient;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PatientRecordMapper {

    @Autowired
    private ModelMapper modelMapper;

    public PatientRecordDTO toDTO(Patient patient, List<ExamResponseDTO> exams, List<AppointmentResponseDTO> appointments) {
        PatientRecordDTO patientRecordDTO = new PatientRecordDTO();
        patientRecordDTO.setPatient(modelMapper.map(patient, PatientResponseDTO.class));
        patientRecordDTO.setExams(exams);
        patientRecordDTO.setAppointments(appointments);
        return patientRecordDTO;
    }
}