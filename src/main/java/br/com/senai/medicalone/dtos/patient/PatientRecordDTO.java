package br.com.senai.medicalone.dtos.patient;

import br.com.senai.medicalone.dtos.exam.ExamResponseDTO;
import br.com.senai.medicalone.dtos.appointment.AppointmentResponseDTO;
import lombok.Data;

import java.util.List;

@Data
public class PatientRecordDTO {
    private PatientResponseDTO patient;
    private List<ExamResponseDTO> exams;
    private List<AppointmentResponseDTO> appointments;
}