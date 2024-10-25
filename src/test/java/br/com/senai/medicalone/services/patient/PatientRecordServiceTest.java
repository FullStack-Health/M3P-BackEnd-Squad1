package br.com.senai.medicalone.services.patient;

import br.com.senai.medicalone.dtos.appointment.AppointmentResponseDTO;
import br.com.senai.medicalone.dtos.exam.ExamResponseDTO;
import br.com.senai.medicalone.dtos.patient.PatientRecordDTO;
import br.com.senai.medicalone.entities.patient.Patient;
import br.com.senai.medicalone.exceptions.customexceptions.PatientNotFoundException;
import br.com.senai.medicalone.mappers.patient.PatientRecordMapper;
import br.com.senai.medicalone.repositories.patient.PatientRepository;
import br.com.senai.medicalone.services.appointment.AppointmentService;
import br.com.senai.medicalone.services.exam.ExamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientRecordServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private ExamService examService;

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private PatientRecordMapper patientRecordMapper;

    @InjectMocks
    private PatientRecordService patientRecordService;

    private Patient patient;
    private PatientRecordDTO patientRecordDTO;
    private List<AppointmentResponseDTO> appointments;
    private List<ExamResponseDTO> exams;

    @BeforeEach
    public void setUp() {
        patient = new Patient();
        patient.setId(1L);

        patientRecordDTO = new PatientRecordDTO();

        appointments = Collections.emptyList();
        exams = Collections.emptyList();
    }

    @Test
    public void testGetPatientRecord_Success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(examService.getExamsByPatientId(1L, Pageable.unpaged())).thenReturn(new PageImpl<>(exams));
        when(appointmentService.getAppointmentsByPatientId(1L, Pageable.unpaged())).thenReturn(new PageImpl<>(appointments));
        when(patientRecordMapper.toDTO(patient, exams, appointments)).thenReturn(patientRecordDTO);

        PatientRecordDTO result = patientRecordService.getPatientRecord(1L);

        assertNotNull(result);
        verify(patientRepository, times(1)).findById(1L);
        verify(examService, times(1)).getExamsByPatientId(1L, Pageable.unpaged());
        verify(appointmentService, times(1)).getAppointmentsByPatientId(1L, Pageable.unpaged());
        verify(patientRecordMapper, times(1)).toDTO(patient, exams, appointments);
    }

    @Test
    public void testGetPatientRecord_PatientNotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PatientNotFoundException.class, () -> patientRecordService.getPatientRecord(1L));
        verify(patientRepository, times(1)).findById(1L);
        verify(examService, never()).getExamsByPatientId(anyLong(), any(Pageable.class));
        verify(appointmentService, never()).getAppointmentsByPatientId(anyLong(), any(Pageable.class));
        verify(patientRecordMapper, never()).toDTO(any(Patient.class), anyList(), anyList());
    }

    @Test
    public void testGetAllPatientRecords_Success() {
        Pageable pageable = mock(Pageable.class);
        Page<Patient> patientsPage = new PageImpl<>(List.of(patient));
        when(patientRepository.findAll(pageable)).thenReturn(patientsPage);
        when(examService.getExamsByPatientId(1L, Pageable.unpaged())).thenReturn(new PageImpl<>(exams));
        when(appointmentService.getAppointmentsByPatientId(1L, Pageable.unpaged())).thenReturn(new PageImpl<>(appointments));
        when(patientRecordMapper.toDTO(patient, exams, appointments)).thenReturn(patientRecordDTO);

        Page<PatientRecordDTO> result = patientRecordService.getAllPatientRecords(null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(patientRepository, times(1)).findAll(pageable);
        verify(examService, times(1)).getExamsByPatientId(1L, Pageable.unpaged());
        verify(appointmentService, times(1)).getAppointmentsByPatientId(1L, Pageable.unpaged());
        verify(patientRecordMapper, times(1)).toDTO(patient, exams, appointments);
    }
}