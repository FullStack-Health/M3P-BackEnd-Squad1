package br.com.senai.medicalone.dtos.record;

import br.com.senai.medicalone.dtos.exam.ExamResponseDTO;
import br.com.senai.medicalone.dtos.appointment.AppointmentResponseDTO;
import br.com.senai.medicalone.dtos.patient.PatientResponseDTO;
import lombok.Data;
import java.util.List;

@Data
public class RecordResponseDTO {
    private PatientResponseDTO patient;
    private List<ExamResponseDTO> exams;
    private List<AppointmentResponseDTO> appointments;
}