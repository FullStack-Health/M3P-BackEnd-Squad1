package br.com.senai.medicalone.services.appointment;

import br.com.senai.medicalone.dtos.appointment.AppointmentRequestDTO;
import br.com.senai.medicalone.dtos.appointment.AppointmentResponseDTO;
import br.com.senai.medicalone.entities.appointment.Appointment;
import br.com.senai.medicalone.exceptions.customexceptions.AppointmentNotFoundException;
import br.com.senai.medicalone.exceptions.customexceptions.BadRequestException;
import br.com.senai.medicalone.mappers.appointment.AppointmentMapper;
import br.com.senai.medicalone.repositories.appointment.AppointmentRepository;
import br.com.senai.medicalone.repositories.patient.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentMapper appointmentMapper;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAppointment_Success() {
        AppointmentRequestDTO requestDTO = new AppointmentRequestDTO();
        requestDTO.setAppointmentReason("Routine Checkup");
        requestDTO.setAppointmentDate(LocalDate.now());
        requestDTO.setAppointmentTime(LocalTime.now());
        requestDTO.setProblemDescription("No issues");
        requestDTO.setPrescribedMedication("None");
        requestDTO.setObservations("None");
        requestDTO.setPatientId(1L);

        Appointment appointment = new Appointment();
        appointment.setId(1L);

        when(patientRepository.existsById(1L)).thenReturn(true);
        when(appointmentMapper.toEntity(any(AppointmentRequestDTO.class))).thenReturn(appointment);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(appointmentMapper.toResponseDTO(any(Appointment.class))).thenReturn(new AppointmentResponseDTO());

        AppointmentResponseDTO responseDTO = appointmentService.createAppointment(requestDTO);

        assertNotNull(responseDTO);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void createAppointment_MissingReason_ShouldThrowException() {
        AppointmentRequestDTO requestDTO = new AppointmentRequestDTO();
        requestDTO.setAppointmentDate(LocalDate.now());
        requestDTO.setAppointmentTime(LocalTime.now());
        requestDTO.setProblemDescription("No issues");
        requestDTO.setPrescribedMedication("None");
        requestDTO.setObservations("None");
        requestDTO.setPatientId(1L);

        assertThrows(BadRequestException.class, () -> appointmentService.createAppointment(requestDTO));
    }

    @Test
    void createAppointment_PatientNotFound_ShouldThrowException() {
        AppointmentRequestDTO requestDTO = new AppointmentRequestDTO();
        requestDTO.setAppointmentReason("Routine Checkup");
        requestDTO.setAppointmentDate(LocalDate.now());
        requestDTO.setAppointmentTime(LocalTime.now());
        requestDTO.setProblemDescription("No issues");
        requestDTO.setPrescribedMedication("None");
        requestDTO.setObservations("None");
        requestDTO.setPatientId(1L);

        when(patientRepository.existsById(1L)).thenReturn(false);

        assertThrows(BadRequestException.class, () -> appointmentService.createAppointment(requestDTO));
    }

    @Test
    void getAppointmentById_Success() {
        Long id = 1L;
        Appointment appointment = new Appointment();
        appointment.setId(id);

        when(appointmentRepository.findById(id)).thenReturn(Optional.of(appointment));
        when(appointmentMapper.toResponseDTO(any(Appointment.class))).thenReturn(new AppointmentResponseDTO());

        AppointmentResponseDTO responseDTO = appointmentService.getAppointmentById(id);

        assertNotNull(responseDTO);
    }

    @Test
    void getAppointmentById_NotFound() {
        Long id = 1L;

        when(appointmentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(AppointmentNotFoundException.class, () -> appointmentService.getAppointmentById(id));
    }

    @Test
    void updateAppointment_Success() {
        Long id = 1L;
        AppointmentRequestDTO requestDTO = new AppointmentRequestDTO();
        requestDTO.setAppointmentReason("Updated Reason");
        requestDTO.setAppointmentDate(LocalDate.now());
        requestDTO.setAppointmentTime(LocalTime.now());
        requestDTO.setProblemDescription("Updated Description");
        requestDTO.setPrescribedMedication("Updated Medication");
        requestDTO.setObservations("Updated Observations");
        requestDTO.setPatientId(1L);

        Appointment existingAppointment = new Appointment();
        existingAppointment.setId(id);

        when(appointmentRepository.findById(id)).thenReturn(Optional.of(existingAppointment));
        when(patientRepository.existsById(1L)).thenReturn(true);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(existingAppointment);
        when(appointmentMapper.toResponseDTO(any(Appointment.class))).thenReturn(new AppointmentResponseDTO());

        AppointmentResponseDTO responseDTO = appointmentService.updateAppointment(id, requestDTO);

        assertNotNull(responseDTO);
    }

    @Test
    void updateAppointment_NotFound() {
        Long id = 1L;
        AppointmentRequestDTO requestDTO = new AppointmentRequestDTO();
        requestDTO.setAppointmentReason("Routine Checkup");
        requestDTO.setAppointmentDate(LocalDate.now());
        requestDTO.setAppointmentTime(LocalTime.now());
        requestDTO.setProblemDescription("No issues");
        requestDTO.setPatientId(1L);

        when(appointmentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> appointmentService.updateAppointment(id, requestDTO));
    }

    @Test
    void deleteAppointment_Success() {
        Long id = 1L;

        when(appointmentRepository.existsById(id)).thenReturn(true);

        appointmentService.deleteAppointment(id);

        verify(appointmentRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteAppointment_NotFound() {
        Long id = 1L;

        when(appointmentRepository.existsById(id)).thenReturn(false);

        assertThrows(AppointmentNotFoundException.class, () -> appointmentService.deleteAppointment(id));
    }

    @Test
    void listAppointments_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Appointment> appointments = List.of(new Appointment(), new Appointment());
        Page<Appointment> appointmentPage = new PageImpl<>(appointments, pageable, appointments.size());

        when(appointmentRepository.findAll(pageable)).thenReturn(appointmentPage);
        when(appointmentMapper.toResponseDTO(any(Appointment.class))).thenReturn(new AppointmentResponseDTO());

        Page<AppointmentResponseDTO> responseDTOs = appointmentService.listAppointments(null, null, pageable);

        assertNotNull(responseDTOs);
        assertEquals(2, responseDTOs.getTotalElements());
    }

    @Test
    void listAppointments_NoAppointmentsFound() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Appointment> appointmentPage = new PageImpl<>(List.of(), pageable, 0);

        when(appointmentRepository.findAll(pageable)).thenReturn(appointmentPage);

        Page<AppointmentResponseDTO> responseDTOs = appointmentService.listAppointments(null, null, pageable);

        assertNotNull(responseDTOs);
        assertTrue(responseDTOs.isEmpty());
    }

    @Test
    void getAppointmentsByPatientId_Success() {
        Long patientId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Appointment> appointments = List.of(new Appointment(), new Appointment());
        Page<Appointment> appointmentPage = new PageImpl<>(appointments, pageable, appointments.size());

        when(appointmentRepository.findByPatientId(patientId, pageable)).thenReturn(appointmentPage);
        when(appointmentMapper.toResponseDTO(any(Appointment.class))).thenReturn(new AppointmentResponseDTO());

        Page<AppointmentResponseDTO> responseDTOs = appointmentService.getAppointmentsByPatientId(patientId, pageable);

        assertNotNull(responseDTOs);
        assertEquals(2, responseDTOs.getTotalElements());
    }
}